package com.address.service.impl;

import com.address.client.EmployeeClient;
import com.address.model.dto.AddressDto;
import com.address.model.dto.AddressRequest;
import com.address.model.dto.AddressRequestDto;
import com.address.model.dto.EmployeeDto;
import com.address.model.entity.Address;
import com.address.repository.AddressRepository;
import com.address.service.AddressService;

import com.address.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AddressServiceImpl implements AddressService {

    Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;

    private final ModelMapper modelMapper;

    private final EmployeeClient employeeClient;

    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper,
                              EmployeeClient employeeClient) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
        this.employeeClient = employeeClient;
    }

    @Override
    public List<AddressDto> saveAddress(AddressRequest addressRequest) {
        employeeClient.getSingleEmployee(addressRequest.getEmpId());
//        System.out.println("address "+employee);
//        if (employee == null) {
//            throw new ResourceNotFoundException("Employee not found with employee id " + addressRequest.getEmpId());
//        }
        List<Address> addressList = this.saveOrUpdateAddressRequest(addressRequest);
        List<Address> savedAddressList = addressRepository.saveAll(addressList);
        return savedAddressList.stream().map(address -> modelMapper.map(address, AddressDto.class)).toList();
    }

    @Override
    public List<AddressDto> updateAddress(AddressRequest addressRequest) {
        employeeClient.getSingleEmployee(addressRequest.getEmpId());
        List<Address> addressListByImpId = addressRepository.findAllByEmpId(addressRequest.getEmpId());
        if (addressListByImpId.isEmpty()) {
            log.info("no address found for this emp Id: {} ", addressRequest.getEmpId());
            log.info("creating new address for this emp Id: {} ", addressRequest.getEmpId());
        }
        List<Address> listToUpdate = this.saveOrUpdateAddressRequest(addressRequest);
        List<Long> upcomingNonNullIds = listToUpdate.stream().map(Address::getId).filter(Objects::nonNull).toList();
        List<Long> existingIds = addressListByImpId.stream().map(Address::getId).toList();
        List<Long> idsToDelete = existingIds.stream().filter(id -> !upcomingNonNullIds.contains(id)).toList();
        if (!idsToDelete.isEmpty()) {
            addressRepository.deleteAllById(idsToDelete);
        }

        List<Address> updatedAddress = addressRepository.saveAll(listToUpdate);
        return updatedAddress.stream().map(address -> modelMapper.map(address, AddressDto.class)).toList();

    }

    @Override
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found with id " + id));
        addressRepository.delete(address);
    }

    @Override
    public AddressDto getSingleAddress(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found with id " + id));
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAllAddress() {
        List<Address> all = addressRepository.findAll();
        if (all.isEmpty()) {
            throw new ResourceNotFoundException("Address not found");
        }
        return all.stream().map(address -> modelMapper.map(address, AddressDto.class)).toList();
    }

    @Override
    public List<AddressDto> getAddressbyEmpId(Long empId) {
        List<Address> addressListByImpId = addressRepository.findAllByEmpId(empId);
        return addressListByImpId.stream().map(address -> modelMapper.map(address, AddressDto.class)).toList();
    }

    private List<Address> saveOrUpdateAddressRequest(AddressRequest addressRequest) {
        List<Address> addressList = new ArrayList<>();
        List<AddressRequestDto> addressRequestDtoList = addressRequest.getAddressRequestDtoList();
        for (AddressRequestDto addressRequestDto : addressRequestDtoList) {
            Address address = new Address();
            address.setId(addressRequestDto.getId());
            address.setEmpId(addressRequest.getEmpId());
            address.setStreet(addressRequestDto.getStreet());
            address.setCity(addressRequestDto.getCity());
            address.setCountry(addressRequestDto.getCountry());
            address.setPinCode(addressRequestDto.getPinCode());
            address.setAddressType(addressRequestDto.getAddressType());
            addressList.add(address);
        }
        return addressList;
    }


}
