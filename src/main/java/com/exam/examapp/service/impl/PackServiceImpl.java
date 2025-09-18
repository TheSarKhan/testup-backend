package com.exam.examapp.service.impl;

import com.exam.examapp.dto.request.PackRequest;
import com.exam.examapp.dto.request.PackUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.PackMapper;
import com.exam.examapp.model.Pack;
import com.exam.examapp.repository.PackRepository;
import com.exam.examapp.service.interfaces.PackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PackServiceImpl implements PackService {
    private final PackRepository packRepository;

    @Override
    public void createPack(PackRequest request) {
        if (packRepository.existsPackByPackName(request.packName()))
            throw new BadRequestException("Pack with name " + request.packName() + " already exists.");
        Pack pack = PackMapper.requestTo(request);
        packRepository.save(pack);
    }

    @Override
    public List<Pack> getAllPacks() {
        return packRepository.findAll();
    }

    @Override
    public Pack getPackById(UUID id) {
        return packRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Pack not found."));
    }

    @Override
    public Pack getPackByName(String packName) {
        return packRepository.getPackByPackName(packName).orElseThrow(()->
                new ResourceNotFoundException("Pack not found."));
    }

    @Override
    public void updatePack(PackUpdateRequest request) {
        Optional<Pack> packByPackName = packRepository.getPackByPackName(request.packName());
        Pack pack = getPackById(request.id());

        if (packByPackName.isPresent() && !packByPackName.get().getId().equals(request.id()))
            throw new BadRequestException("Pack with name " + request.packName() + " already exists.");

        if ("Free".equals(pack.getPackName()) && !"Free".equals(request.packName()))
            throw new BadRequestException("Free pack name cannot be updated.");
        Pack updatedPack = PackMapper.updateRequestTo(pack, request);
        packRepository.save(updatedPack);
    }

    @Override
    public void deletePack(UUID id) {
        packRepository.deleteById(id);
    }
}
