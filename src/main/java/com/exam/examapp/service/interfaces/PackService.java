package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.PackRequest;
import com.exam.examapp.dto.request.PackUpdateRequest;
import com.exam.examapp.model.Pack;

import java.util.List;
import java.util.UUID;

public interface PackService {
    void createPack(PackRequest request);

    List<Pack> getAllPacks();

    Pack getPackById(UUID id);

    Pack getPackByName(String packName);

    List<String> getPackNames();

    void updatePack(PackUpdateRequest request);

    void deletePack(UUID id);
}
