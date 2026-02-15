package org.example.testmentorbackend.services;

import org.example.testmentorbackend.model.entity.Options;

public interface OptionService {
    Options AddOptions(Options options);

    Options findById(Long Id);

}