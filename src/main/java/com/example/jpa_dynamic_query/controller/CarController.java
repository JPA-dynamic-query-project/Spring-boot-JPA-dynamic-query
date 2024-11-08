package com.example.jpa_dynamic_query.controller;

import com.example.jpa_dynamic_query.dto.request.CarRequestDTO;
import com.example.jpa_dynamic_query.dto.request.SearchCarRequestDTO;
import com.example.jpa_dynamic_query.exception.AppException;
import com.example.jpa_dynamic_query.response.DataResponse;
import com.example.jpa_dynamic_query.response.ErrorResponse;
import com.example.jpa_dynamic_query.service.CarService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;
    @PostMapping("/search-by-criteria")
    public ResponseEntity<?> searchCarByCriteria(
            @RequestBody SearchCarRequestDTO searchCarRequestDTO
    ){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(
                            HttpStatus.OK.value(),
                            "Get car successfully",
                            carService.findCarWithCriteria(searchCarRequestDTO)
                    )
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/search-by-entity-manager")
    public ResponseEntity<?> searchCarByEntityManager(
            @RequestBody SearchCarRequestDTO searchCarRequestDTO
    ){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(
                            HttpStatus.OK.value(),
                            "Get car successfully",
                            carService.findCarWithEntityManager(searchCarRequestDTO)
                    )
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/search-by-procedure")
    public ResponseEntity<?> searchCarByProcedure(
            @RequestBody SearchCarRequestDTO searchCarRequestDTO
    ){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(
                            HttpStatus.OK.value(),
                            "Get car successfully",
                            carService.searchCarWithProcedure(searchCarRequestDTO)
                    )
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping
    public ResponseEntity<?> addCar(@RequestBody CarRequestDTO carRequestDTO){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            "Add car successfully",
                            carService.addCar(carRequestDTO))
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/add-car-procedure")
    public ResponseEntity<?> addCarWithProcedure(@RequestBody CarRequestDTO carRequestDTO){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            "Add car successfully",
                            carService.insertCarWithProcedure(carRequestDTO))
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/get-car")
    public ResponseEntity<?> getCar(@RequestBody CarRequestDTO carRequestDTO) {
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            String.format("Get car with id %s successfully", carRequestDTO.getId()),
                            carService.getCar(carRequestDTO))
            );
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/get-car-procedure")
    public ResponseEntity<?> getCarWithProcedure(@RequestBody CarRequestDTO carRequestDTO) {
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            String.format("Get car with id %s successfully", carRequestDTO.getId()),
                            carService.getCarWithProcedure(carRequestDTO))
            );
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @GetMapping("/list")
    public ResponseEntity<?> getAllCar
            (@RequestParam(defaultValue = "1", required = false) int pageNo,
             @Min(1) @RequestParam(defaultValue = "2", required = false) int pageSize,
             @RequestParam(required = false) String sortBy) {
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            "Get list car successfully",
                            carService.getAllCar(pageNo, pageSize, sortBy))
            );
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }


    @PostMapping("update-car")
    public ResponseEntity<?> updateCar(
            @RequestBody CarRequestDTO carRequestDTO){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            String.format("Update car with id %s successfully", carRequestDTO.getId()),
                            carService.updateCar(carRequestDTO))
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/update-car-procedure")
    public ResponseEntity<?> updateCarWithProcedure(
            @RequestBody CarRequestDTO carRequestDTO){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            String.format("Update car with id %s successfully", carRequestDTO.getId()),
                            carService.updateCarWithProcedure(carRequestDTO))
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("delete-car")
    public ResponseEntity<?> deleteCar(@RequestBody CarRequestDTO carRequestDTO){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            carService.deleteCar(carRequestDTO))
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/delete-car-procedure")
    public ResponseEntity<?> deleteCarWithProcedure(@RequestBody CarRequestDTO carRequestDTO){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(HttpStatus.OK.value(),
                            carService.deleteCarWithProcedure(carRequestDTO))
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
}
