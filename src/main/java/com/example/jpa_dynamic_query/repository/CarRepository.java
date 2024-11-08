package com.example.jpa_dynamic_query.repository;

import com.example.jpa_dynamic_query.dto.response.CarResponseDTO;
import com.example.jpa_dynamic_query.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car>{
    @Procedure(procedureName = "SEARCH_CAR_JSON")
    List<Car> getCarByProcedure(String model);

    @Procedure(procedureName = "COUNT_CAR_JSON")
    int countCarSearch(String model, int count);

}
