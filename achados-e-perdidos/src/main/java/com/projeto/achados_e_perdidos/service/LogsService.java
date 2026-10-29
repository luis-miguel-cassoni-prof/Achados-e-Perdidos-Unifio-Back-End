package com.projeto.achados_e_perdidos.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.achados_e_perdidos.model.Logs;
import com.projeto.achados_e_perdidos.repository.LogsRepository;
import com.projeto.achados_e_perdidos.repository.UsuarioRepository;

@Service
public class LogsService {

    private final LogsRepository logsRepository;
    private final UsuarioRepository usuarioRepository;

    public LogsService(LogsRepository logsRepository, UsuarioRepository usuarioRepository) {
        this.logsRepository = logsRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void registrar(Long idUsuario, String acao) {
        Logs log = new Logs();
        if (idUsuario != null) {
            log.setIdUsuario(usuarioRepository.getReferenceById(idUsuario));
        }
        log.setAcao(acao);
        logsRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<Logs> listar(Pageable pageable) {
        return logsRepository.findAll(pageable);
    }
}
