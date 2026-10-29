package com.projeto.achados_e_perdidos.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.achados_e_perdidos.model.Usuario;
import com.projeto.achados_e_perdidos.model.enums.NivelAcesso;
import com.projeto.achados_e_perdidos.repository.LogsRepository;
import com.projeto.achados_e_perdidos.repository.PublicacaoRepository;
import com.projeto.achados_e_perdidos.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PublicacaoRepository publicacaoRepository;
    private final LogsRepository logsRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            PublicacaoRepository publicacaoRepository,
            LogsRepository logsRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.publicacaoRepository = publicacaoRepository;
        this.logsRepository = logsRepository;
    }

    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Os dados do usuário são obrigatórios.");
        }
        validarSenha(usuario.getHashSenha());
        usuario.setId(null);
        usuario.setEmail(usuario.getEmail().trim().toLowerCase(Locale.ROOT));
        usuario.setBloqueado(false);
        usuario.setNivelDeAcesso(NivelAcesso.USUARIO);
        usuario.setHashSenha(passwordEncoder.encode(usuario.getHashSenha()));
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos."));

        if (usuario.isBloqueado() || !passwordEncoder.matches(senha, usuario.getHashSenha())) {
            throw new BadCredentialsException("Email ou senha inválidos.");
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
    }

    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        if (usuarioAtualizado == null) {
            throw new IllegalArgumentException("Os dados do usuário são obrigatórios.");
        }

        Usuario usuario = buscarUsuarioPorId(id);
        usuario.setNomeUsuario(usuarioAtualizado.getNomeUsuario());
        usuario.setEmail(usuarioAtualizado.getEmail().trim().toLowerCase(Locale.ROOT));
        usuario.setUrlFotoPerfil(usuarioAtualizado.getUrlFotoPerfil());
        usuario.setTelefone(usuarioAtualizado.getTelefone());

        if (usuarioAtualizado.getHashSenha() != null && !usuarioAtualizado.getHashSenha().isBlank()) {
            validarSenha(usuarioAtualizado.getHashSenha());
            usuario.setHashSenha(passwordEncoder.encode(usuarioAtualizado.getHashSenha()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletarUsuario(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        publicacaoRepository.desvincularUsuario(id);
        logsRepository.desvincularUsuario(id);
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public Usuario alterarBloqueio(Long id, boolean bloqueado) {
        Usuario usuario = buscarUsuarioPorId(id);
        usuario.setBloqueado(bloqueado);
        return usuarioRepository.save(usuario);
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isBlank() || senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("A senha não pode exceder 72 bytes em UTF-8.");
        }
    }
}
