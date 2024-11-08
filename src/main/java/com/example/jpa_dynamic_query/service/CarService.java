package com.example.jpa_dynamic_query.service;

import com.example.jpa_dynamic_query.dto.request.CarRequestDTO;
import com.example.jpa_dynamic_query.dto.response.CarResponseDTO;
import com.example.jpa_dynamic_query.dto.request.SearchCarRequestDTO;
import com.example.jpa_dynamic_query.response.PageResponse;
import org.hibernate.Internal;

import java.util.List;


public interface CarService {
    PageResponse<?> findCarWithCriteria(SearchCarRequestDTO searchCarRequestDTO) throws Exception;
    PageResponse<?> findCarWithEntityManager(SearchCarRequestDTO searchCarRequestDTO);
    PageResponse<?> findCarWithProcedure(SearchCarRequestDTO searchCarRequestDTO);
    CarResponseDTO addCar(CarRequestDTO carRequestDTO);

    CarResponseDTO insertCarWithProcedure(CarRequestDTO carRequestDTO);
    CarResponseDTO getCar(CarRequestDTO carRequestDTO);
    CarResponseDTO getCarWithProcedure(CarRequestDTO carRequestDTO);
    PageResponse<?> searchCarWithProcedure(SearchCarRequestDTO searchCarRequestDTO);
    String deleteCarWithProcedure(CarRequestDTO carRequestDTO);
    PageResponse<?> getAllCar(int pageNo, int pageSize, String sortBy);
    CarResponseDTO updateCar(CarRequestDTO carRequestDTO);

    CarResponseDTO updateCarWithProcedure(CarRequestDTO carRequestDTO);
    String deleteCar(CarRequestDTO carRequestDTO);
}
