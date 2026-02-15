package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.Options;
import org.example.testmentorbackend.repository.OptionRepository;
import org.example.testmentorbackend.services.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;

    @Autowired
    public OptionServiceImpl(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Override
    public Options AddOptions(Options options) {
        return this.optionRepository.save(options);
    }

    @Override
    public Options findById(Long id) {
        return this.optionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Option not found: " + id));
    }
}
