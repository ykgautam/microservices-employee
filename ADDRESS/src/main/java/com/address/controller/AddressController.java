package com.address.controller;

import com.address.model.dto.AddressDto;
import com.address.model.dto.AddressRequest;
import com.address.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/save")
    public ResponseEntity<List<AddressDto>> saveEmployee(@RequestBody AddressRequest addressRequest) {
        System.out.println(addressRequest.getEmpId());
        List<AddressDto> addressDtos = addressService.saveAddress(addressRequest);
        return new ResponseEntity<List<AddressDto>>(addressDtos, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<List<AddressDto>> updateEmployee(@RequestBody AddressRequest addressRequest) {
        List<AddressDto> response = addressService.updateAddress(addressRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all-address")
    public ResponseEntity<List<AddressDto>> getAllAddress() {
        return new ResponseEntity<>(addressService.getAllAddress(), HttpStatus.OK);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDto> getSingleAddress(@PathVariable("addressId") Long addressId) {
        AddressDto singleAddress = addressService.getSingleAddress(addressId);
        return new ResponseEntity<>(singleAddress, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return new ResponseEntity<>("Address deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/empId/{empId}")
    public ResponseEntity<List<AddressDto>> getAddressByEmpId(@PathVariable Long empId) {
        List<AddressDto> response = addressService.getAddressbyEmpId(empId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
