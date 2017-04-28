package ru.belyaeva.rsoi.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.belyaeva.rsoi.model.*;
import ru.belyaeva.rsoi.domain.DeliveryEntity;
import ru.belyaeva.rsoi.repository.DeliveryRepository;


import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by user on 22.11.2016.
 */
@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    DeliveryRepository deliveryRepository;

    // + | -
    @SneakyThrows
    @Override
    public DeliveryResponse updateBillingDelivery(Long deliveryId, UpdateBillingDelivery updateBillingDelivery){
        DeliveryEntity deliveryEntity = deliveryRepository.findOne(deliveryId);
        if (deliveryEntity == null) {
            throw new EntityNotFoundException("Delivery with id = " + deliveryId + "is not found");
        }
        deliveryEntity.setPaid(updateBillingDelivery.getPaid());
        deliveryEntity.setBilling(updateBillingDelivery.getBillingId());
        deliveryRepository.save(deliveryEntity);

        DeliveryResponse deliveryResponse = new DeliveryResponse();

        deliveryResponse.setDelivery(getDelivery(deliveryEntity));
        deliveryResponse.setCode(true);
        return deliveryResponse;
    }
    // + | -
    @SneakyThrows
    @Override
    public Delivery getDelivery(DeliveryEntity deliveryEntity)
    {
        Delivery delivery = new Delivery();

        delivery.setId(deliveryEntity.getId());
        delivery.setUserId(deliveryEntity.getUserId());
        delivery.setCourierId(deliveryEntity.getCourierId());
        delivery.setName(deliveryEntity.getName());
        delivery.setWeight(deliveryEntity.getWeight());
        delivery.setWidth(deliveryEntity.getWidth());
        delivery.setHeight(deliveryEntity.getHeight());
        delivery.setDeep(deliveryEntity.getDeep());
        delivery.setTimeDelivery(deliveryEntity.getTimeDelivery());
        delivery.setAddressStart(deliveryEntity.getAddressStart());
        delivery.setAddressFinish(deliveryEntity.getAddressFinish());
        delivery.setDescription(deliveryEntity.getDescription());
        if (deliveryEntity.getPaid()==null)
            delivery.setPaid(false);
        else
            delivery.setPaid(deliveryEntity.getPaid());
        delivery.setBilling(deliveryEntity.getBilling());
        delivery.setTracks(deliveryEntity.getTracks());

        return delivery;
    }
    // + | -
    @SneakyThrows
    @Override
    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
        DeliveryEntity deliveryEntity = new DeliveryEntity();

        deliveryEntity.setBilling(null);
        deliveryEntity.setUserId(request.getUserId());
        deliveryEntity.setCourierId(null);
        deliveryEntity.setPaid(false);
        deliveryEntity.setName(request.getName());
        deliveryEntity.setWeight(request.getWeight());
        deliveryEntity.setWidth(request.getWidth());
        deliveryEntity.setHeight(request.getHeight());
        deliveryEntity.setDeep(request.getDeep());
        deliveryEntity.setTimeDelivery(request.getTimeDelivery());
        deliveryEntity.setAddressStart(request.getAddressStart());
        deliveryEntity.setAddressFinish(request.getAddressFinish());
        deliveryEntity.setDescription(request.getDescription());

        deliveryRepository.save(deliveryEntity);

        DeliveryResponse response = new DeliveryResponse();
        Delivery delivery = new Delivery();

        response.setDelivery(getDelivery(deliveryEntity));
        response.setCode(true);
        return response;
    }

    // + | -
    @Override
    public DeliveryResponse getDelivery(Long deliveryId) {

        DeliveryEntity deliveryEntity = deliveryRepository.findOne(deliveryId);
        if (deliveryEntity == null) {
            throw new EntityNotFoundException("Delivery with id = " + deliveryId + "is not found");
        }

        DeliveryResponse deliveryResponse = new DeliveryResponse();

        deliveryResponse.setDelivery(getDelivery(deliveryEntity));
        deliveryResponse.setCode(true);
        return deliveryResponse;
    }
    @Override
    public BaseResponse deleteDelivery(Long deliveryId)
    {
        DeliveryEntity deliveryEntity = deliveryRepository.findOne(deliveryId);

        if (deliveryEntity == null){
            throw new EntityNotFoundException("Delivery with id = " + deliveryId + "is not found");
        }

        deliveryRepository.delete(deliveryId);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrorCode(true);
        baseResponse.setErrorMessage("");
        return baseResponse;
    }

    // + | -
    @Override
    public DeliveryResponse updateDelivery(UpdateDeliveryRequest delivery) {

        DeliveryEntity deliveryEntity = deliveryRepository.findOne(delivery.getId());
        if (deliveryEntity == null) {
            throw new EntityNotFoundException("Delivery with id = " + delivery.getId() + " is not found");
        }
        deliveryEntity.setCourierId(delivery.getCourierId());
        deliveryEntity.setName(delivery.getName());
        deliveryEntity.setWeight(delivery.getWeight());
        deliveryEntity.setWidth(delivery.getWidth());
        deliveryEntity.setHeight(delivery.getHeight());
        deliveryEntity.setDeep(delivery.getDeep());
        deliveryEntity.setTimeDelivery(delivery.getTimeDelivery());
        deliveryEntity.setAddressStart(delivery.getAddressStart());
        deliveryEntity.setAddressFinish(delivery.getAddressFinish());
        deliveryEntity.setDescription(delivery.getDescription());

        deliveryRepository.save(deliveryEntity);

        DeliveryResponse deliveryResponse = new DeliveryResponse();
        deliveryResponse.setCode(true);
        deliveryResponse.setDelivery(getDelivery(deliveryEntity));
        return deliveryResponse;
    }
    // + | -
    @SneakyThrows
    @Override
    public DeliveryResponse putToDelivery(Long deliveryId, Long trackId) {

        DeliveryEntity deliveryEntity = deliveryRepository.findOne(deliveryId);
        if (deliveryEntity == null) {
            throw new EntityNotFoundException("Delivery with id = " + deliveryId + " is not found");
        }

        deliveryEntity.getTracks().add(trackId);
        deliveryRepository.save(deliveryEntity);

        DeliveryResponse deliveryResponse = new DeliveryResponse();
        deliveryResponse.setDelivery(getDelivery(deliveryEntity));
        deliveryResponse.setCode(true);

        return deliveryResponse;
    }
    // + | -
    @Override
    public DeliveryResponse deleteFromDelivery(Long deliveryId, Long trackId) {

        DeliveryEntity deliveryEntity = deliveryRepository.findOne(deliveryId);
        if (deliveryEntity == null) {
            throw new EntityNotFoundException("Delivery with id = " + deliveryId + " is not found");
        }
        deliveryEntity.getTracks().remove(trackId);
        deliveryRepository.save(deliveryEntity);

        DeliveryResponse deliveryResponse = new DeliveryResponse();
        deliveryResponse.setDelivery(getDelivery(deliveryEntity));
        deliveryResponse.setCode(true);

        return deliveryResponse;

    }

    // + | -
    @Override
    public DeliveryListResponse getDeliveries(Long userId, Long page, Long size) {

        Long from = page * size - size;

        List<DeliveryEntity> deliveryEntities = deliveryRepository.findByUserId(userId);
        List<Delivery> deliveries = new ArrayList<>();
        List<DeliveryEntity> newDeliveryEntitites=  deliveryEntities.stream()
                .sorted(Comparator.comparingLong(DeliveryEntity::getId))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        for (DeliveryEntity deliveryEntity : newDeliveryEntitites){
            Delivery delivery = getDelivery(deliveryEntity);
            deliveries.add(delivery);
        }
        DeliveryListResponse response = new DeliveryListResponse();
        response.setCode(true);
        response.setDeliveries(deliveries);
        return response;
    }

    // + | -
    @Override
    public DeliveryListResponse getDeliveriesOfCourier(Long courierId, Long page, Long size) {

        Long from = page * size - size;

        List<DeliveryEntity> deliveryEntities = deliveryRepository.findByCourierId(courierId);
        List<Delivery> deliveries = new ArrayList<>();
        List<DeliveryEntity> newDeliveryEntitites=  deliveryEntities.stream()
                .sorted(Comparator.comparingLong(DeliveryEntity::getId))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        for (DeliveryEntity deliveryEntity : newDeliveryEntitites){
            Delivery delivery = getDelivery(deliveryEntity);
            deliveries.add(delivery);
        }
        DeliveryListResponse response = new DeliveryListResponse();
        response.setCode(true);
        response.setDeliveries(deliveries);
        return response;
    }

    @Override
    public DeliveryListResponse getDeliveriesWithoutCourier(Long page, Long size) {

        Long from = page * size - size;

        List<DeliveryEntity> deliveryEntities = deliveryRepository.findByCourierId(null);
        List<Delivery> deliveries = new ArrayList<>();
        List<DeliveryEntity> newDeliveryEntitites=  deliveryEntities.stream()
                .sorted(Comparator.comparingLong(DeliveryEntity::getId))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        for (DeliveryEntity deliveryEntity : newDeliveryEntitites){
            Delivery delivery = getDelivery(deliveryEntity);
            deliveries.add(delivery);
        }
        DeliveryListResponse response = new DeliveryListResponse();
        response.setCode(true);
        response.setDeliveries(deliveries);
        return response;
    }


}
