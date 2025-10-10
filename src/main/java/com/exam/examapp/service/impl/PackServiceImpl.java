package com.exam.examapp.service.impl;

import com.exam.examapp.dto.request.PackRequest;
import com.exam.examapp.dto.request.PackUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.PackMapper;
import com.exam.examapp.model.Pack;
import com.exam.examapp.repository.PackRepository;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.PackService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackServiceImpl implements PackService {
    private final PackRepository packRepository;

    private final UserService userService;

    private final LogService logService;

    @Override
    public void createPack(PackRequest request) {
        log.info("Paket yaradılır");
        if (packRepository.existsPackByPackName(request.packName()))
            throw new BadRequestException(request.packName() + " adlı paket artıq mövcuddur");
        Pack pack = PackMapper.requestTo(request);
        packRepository.save(pack);
        log.info("Paket yaradıldı");
        logService.save("Paket yaradıldı", userService.getCurrentUserOrNull());
    }

    @Override
    public List<Pack> getAllPacks() {
        return packRepository.findAll();
    }

    @Override
    public Pack getPackById(UUID id) {
        return packRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Paket tapılmadı."));
    }

    @Override
    public Pack getPackByName(String packName) {
        return packRepository.getPackByPackName(packName).orElseThrow(()->
                new ResourceNotFoundException("Paket tapılmadı."));
    }

    @Override
    public void updatePack(PackUpdateRequest request) {
        log.info("Paket yenilənir");
        Optional<Pack> packByPackName = packRepository.getPackByPackName(request.packName());
        Pack pack = getPackById(request.id());

        if (packByPackName.isPresent() && !packByPackName.get().getId().equals(request.id()))
            throw new BadRequestException(request.packName() + " adlı paket artıq mövcuddur");

        if ("Free".equals(pack.getPackName()) && !"Free".equals(request.packName()))
            throw new BadRequestException("Free paketin adı yenilənə bilməz");
        Pack updatedPack = PackMapper.updateRequestTo(pack, request);
        packRepository.save(updatedPack);
        log.info("Paket yeniləndi");
        logService.save("Paket yeniləndi", userService.getCurrentUserOrNull());
    }

    @Override
    public void deletePack(UUID id) {
        log.info("Paket silinir");
        packRepository.deleteById(id);
        log.info("Paket silindi");
        logService.save("Paket silindi", userService.getCurrentUserOrNull());
    }
}
