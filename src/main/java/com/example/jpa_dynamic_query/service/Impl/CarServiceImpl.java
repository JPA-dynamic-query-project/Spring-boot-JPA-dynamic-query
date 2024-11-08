package com.example.jpa_dynamic_query.service.Impl;

import com.example.jpa_dynamic_query.dto.request.CarRequestDTO;
import com.example.jpa_dynamic_query.dto.response.CarResponseDTO;
import com.example.jpa_dynamic_query.dto.request.SearchCarRequestDTO;
import com.example.jpa_dynamic_query.entity.Car;
import com.example.jpa_dynamic_query.exception.AppException;
import com.example.jpa_dynamic_query.mapper.CarMapper;
import com.example.jpa_dynamic_query.repository.CarRepository;
import com.example.jpa_dynamic_query.response.PageResponse;
import com.example.jpa_dynamic_query.service.CarService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {
    @PersistenceContext
    private EntityManager entityManager;

    private final CarRepository carRepository;

    private final CarMapper carMapper;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public PageResponse<?> findCarWithCriteria(SearchCarRequestDTO searchCarRequestDTO) throws Exception {
        if (searchCarRequestDTO.getPageNo() < 0){
            searchCarRequestDTO.setPageNo(1);
        }
        if (searchCarRequestDTO.getPageSize() < 0){
            searchCarRequestDTO.setPageSize(10);
        }
        int pageNo = searchCarRequestDTO.getPageNo();
        int pageSize = searchCarRequestDTO.getPageSize();
        int page_sub = pageNo > 0 ? pageNo - 1 : 0;
        Pageable pageable = PageRequest.of(page_sub, pageSize);
        Page<Car> page = carRepository.findAll(new Specification<Car>() {
            @Override
            public Predicate toPredicate(Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if ((searchCarRequestDTO.getName() != null) && (!searchCarRequestDTO.getName().isBlank())) {
                    predicates.add(criteriaBuilder.like(root.get("name"),
                            "%" + searchCarRequestDTO.getName() + "%"));
                }
                if (searchCarRequestDTO.getBrand() != null && !searchCarRequestDTO.getBrand().isBlank()) {
                    predicates.add(criteriaBuilder.like(root.get("brand"),
                            "%" + searchCarRequestDTO.getBrand() + "%"));
                }
                if (searchCarRequestDTO.getColor() != null && !searchCarRequestDTO.getColor().isBlank()) {
                    predicates.add(criteriaBuilder.like(root.get("color"),
                            "%" + searchCarRequestDTO.getColor() + "%"));
                }
                if(searchCarRequestDTO.getStartDate() != null && searchCarRequestDTO.getEndDate() != null) {
                    predicates.add(criteriaBuilder.between(
                            root.get("dateBooking"),
                            searchCarRequestDTO.getStartDate(),
                            searchCarRequestDTO.getEndDate()
                    ));
                } else if (searchCarRequestDTO.getStartDate() != null || searchCarRequestDTO.getEndDate() != null){
                    if (searchCarRequestDTO.getEndDate() != null){
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateBooking"), searchCarRequestDTO.getEndDate()));
                    }else {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateBooking"), searchCarRequestDTO.getStartDate()));
                    }
                }
                if (searchCarRequestDTO.getField() != null && searchCarRequestDTO.getDirection() != null){
                    if (searchCarRequestDTO.getDirection().equalsIgnoreCase("asc")){
                        query.orderBy(criteriaBuilder.asc(root.get(searchCarRequestDTO.getField())));
                    }else {
                        query.orderBy(criteriaBuilder.desc(root.get(searchCarRequestDTO.getField())));
                    }
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
        List<Car> cars = page.getContent();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .items(cars)
                .build();
    }

    @Override
    public PageResponse<?> findCarWithEntityManager(SearchCarRequestDTO searchCarRequestDTO) {
        if (searchCarRequestDTO.getPageNo() < 0){
            searchCarRequestDTO.setPageNo(1);
        }
        if (searchCarRequestDTO.getPageSize() < 0){
            searchCarRequestDTO.setPageSize(10);
        }
        int pageNo = searchCarRequestDTO.getPageNo();
        int pageSize = searchCarRequestDTO.getPageSize();
        int page_sub = pageNo > 0 ? pageNo - 1 : 0;

        //===========get list car===========
        StringBuilder sqlQuery = new StringBuilder("FROM Car u WHERE 1=1");
        Map<String, Object> parametersSelect = new HashMap<>();
        buildAndVerifyQuery(sqlQuery,parametersSelect, searchCarRequestDTO);
        //create query get list car
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        for (Map.Entry<String, Object> entry : parametersSelect.entrySet()){
            selectQuery.setParameter(entry.getKey(), entry.getValue());
        }

        //set pagination
        selectQuery.setFirstResult(page_sub * pageSize);
        selectQuery.setMaxResults(pageSize);
        List<?> cars = selectQuery.getResultList();

        //======count car========
        StringBuilder sqlCountQuery = sqlQuery.insert(0, "SELECT COUNT(u) ");
        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        for (Map.Entry<String, Object> entry : parametersSelect.entrySet()){
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        Long totalElements =(Long)countQuery.getSingleResult();
        Pageable pageable = PageRequest.of(page_sub, pageSize);

        Page<?> page = new PageImpl<>(cars, pageable, totalElements);
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .items(cars)
                .build();
    }

    @Override
    public PageResponse<?> findCarWithProcedure(SearchCarRequestDTO searchCarRequestDTO) {
        if (searchCarRequestDTO.getPageNo() < 0){
            searchCarRequestDTO.setPageNo(1);
        }
        if (searchCarRequestDTO.getPageSize() < 0){
            searchCarRequestDTO.setPageSize(10);
        }
        int pageNo = searchCarRequestDTO.getPageNo();
        int pageSize = searchCarRequestDTO.getPageSize();
        int page_sub = pageNo > 0 ? pageNo - 1 : 0;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<Car> carList;
        String json;
        try {
            json = objectMapper.writeValueAsString(searchCarRequestDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        StoredProcedureQuery query =entityManager.createStoredProcedureQuery("SEARCH_CAR_JSON", Car.class);
        StoredProcedureQuery queryCount =entityManager.createStoredProcedureQuery("COUNT_CAR_JSON", Car.class);
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, json);
        queryCount.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        queryCount.registerStoredProcedureParameter(2, Integer.class, ParameterMode.OUT);
        queryCount.setParameter(1, json);
        queryCount.execute();
        Integer outCount = (Integer) queryCount.getOutputParameterValue(2);
        carList = query.getResultStream().toList();
        Pageable pageable = PageRequest.of(page_sub, pageSize);

        Page<?> page = new PageImpl<>(carList, pageable, outCount);
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .items(carList)
                .build();
    }

    public void verifySQL(StringBuilder sqlQuery, SearchCarRequestDTO searchCarRequestDTO){
        if (searchCarRequestDTO.getName() != null){
            sqlQuery.append(" AND (u.name) like :name");
        }
        if (searchCarRequestDTO.getBrand() != null){
            sqlQuery.append(" AND (u.brand) like :brand");
        }
        if (searchCarRequestDTO.getColor() != null){
            sqlQuery.append(" AND (u.color) like :color");
        }
        if (searchCarRequestDTO.getStartDate() != null && searchCarRequestDTO.getEndDate() != null){
            sqlQuery.append(" AND u.dateBooking between :startDate AND :endDate");
        }
        if (searchCarRequestDTO.getField() != null && searchCarRequestDTO.getDirection() != null){
            sqlQuery.append(String.format(" ORDER BY u.%s %s",searchCarRequestDTO.getField(),
                    searchCarRequestDTO.getDirection()));
        }
    }
    public void verifyParameter(Query query, SearchCarRequestDTO searchCarRequestDTO){
        if (searchCarRequestDTO.getName() != null) {
            query.setParameter("name", String.format("%%%s%%", searchCarRequestDTO.getName()));
        }
        if (searchCarRequestDTO.getBrand() != null) {
            query.setParameter("brand", String.format("%%%s%%", searchCarRequestDTO.getBrand()));
        }
        if (searchCarRequestDTO.getColor() != null) {
            query.setParameter("color", String.format("%%%s%%", searchCarRequestDTO.getColor()));
        }
        if (searchCarRequestDTO.getStartDate() != null && searchCarRequestDTO.getEndDate() != null){
            query.setParameter("startDate", searchCarRequestDTO.getStartDate());
            query.setParameter("endDate", searchCarRequestDTO.getEndDate());
        }
    }
    public void buildAndVerifyQuery(StringBuilder sqlQuery,
                                    Map<String, Object> parameters,
                                    SearchCarRequestDTO searchCarRequestDTO){

        if (searchCarRequestDTO.getName() != null && !searchCarRequestDTO.getName().isBlank()){
            sqlQuery.append(" AND (u.name) like :name");
            parameters.put("name", String.format("%%%s%%", searchCarRequestDTO.getName()));
        }
        if (searchCarRequestDTO.getBrand() != null && !searchCarRequestDTO.getBrand().isBlank()) {
            sqlQuery.append(" AND (u.brand) like :brand");
            parameters.put("brand", String.format("%%%s%%", searchCarRequestDTO.getBrand()));
        }
        if (searchCarRequestDTO.getColor() != null && !searchCarRequestDTO.getColor()   .isBlank()) {
            sqlQuery.append(" AND (u.color) like :color");
            parameters.put("color", String.format("%%%s%%", searchCarRequestDTO.getColor()));
        }
        if (searchCarRequestDTO.getStartDate() != null && searchCarRequestDTO.getEndDate() != null){
            sqlQuery.append(" AND u.dateBooking BETWEEN :startDate AND :endDate");
            parameters.put("startDate", searchCarRequestDTO.getStartDate());
            parameters.put("endDate", searchCarRequestDTO.getEndDate());
        }else if (searchCarRequestDTO.getStartDate() != null || searchCarRequestDTO.getEndDate() != null){
            if (searchCarRequestDTO.getEndDate() != null){
                sqlQuery.append(" AND u.dateBooking <= :endDate");
                parameters.put("endDate", searchCarRequestDTO.getEndDate());
            }else {
                sqlQuery.append(" AND u.dateBooking >= :startDate");
                parameters.put("startDate", searchCarRequestDTO.getStartDate());
            }

        }
        if (searchCarRequestDTO.getField() != null && searchCarRequestDTO.getDirection() != null) {
            sqlQuery.append(String.format(" ORDER BY u.%s %s", searchCarRequestDTO.getField(),
                    searchCarRequestDTO.getDirection()));
        }
    }

    @Override
    public CarResponseDTO addCar(CarRequestDTO carRequestDTO) {
        Car newCar = carMapper.toCarEntity(carRequestDTO);
        carRepository.save(newCar);
        return carMapper.toCarResponseDTO(newCar);
    }

    @Override
    public CarResponseDTO insertCarWithProcedure(CarRequestDTO carRequestDTO) {
        Car newCar = carMapper.toCarEntity(carRequestDTO);
        StoredProcedureQuery insertCarProcedure = entityManager.createStoredProcedureQuery("CARPROCEDURE.INSERT_CAR");
        insertCarProcedure.registerStoredProcedureParameter("c_brand", String.class, ParameterMode.IN);
        insertCarProcedure.registerStoredProcedureParameter("c_color", String.class, ParameterMode.IN);
        insertCarProcedure.registerStoredProcedureParameter("c_date_booking", Date.class, ParameterMode.IN);
        insertCarProcedure.registerStoredProcedureParameter("c_name", String.class, ParameterMode.IN);


        insertCarProcedure.setParameter("c_brand", newCar.getBrand());
        insertCarProcedure.setParameter("c_color", newCar.getColor());
        insertCarProcedure.setParameter("c_date_booking", newCar.getDateBooking());
        insertCarProcedure.setParameter("c_name", newCar.getName());
        insertCarProcedure.execute();

        return carMapper.toCarResponseDTO(newCar);
    }

    @Override
    public CarResponseDTO getCar(CarRequestDTO carRequestDTO) {
        Car findCar = getCarById(carRequestDTO.getId());
        return carMapper.toCarResponseDTO(findCar);
    }

    @Override
    public CarResponseDTO getCarWithProcedure(CarRequestDTO carRequestDTO) {

        StoredProcedureQuery getCarProcedure = entityManager.createStoredProcedureQuery("CARPROCEDURE.GET_CAR_BY_ID");
        getCarProcedure.registerStoredProcedureParameter("c_id", Integer.class, ParameterMode.IN);
        getCarProcedure.registerStoredProcedureParameter("c_out_cursor", void.class, ParameterMode.REF_CURSOR);

        getCarProcedure.setParameter("c_id", carRequestDTO.getId());
        getCarProcedure.execute();

        List<Object[]> carList = getCarProcedure.getResultList();

        Object[] carData = carList.get(0);

        CarResponseDTO carResponseDTO = new CarResponseDTO();
        carResponseDTO.setId((Integer) carData[0]);
        carResponseDTO.setBrand((String) carData[1]);
        carResponseDTO.setColor((String) carData[2]);
        Timestamp dateBooking = (Timestamp) carData[3];
        carResponseDTO.setDateBooking(String.valueOf(new Date(dateBooking.getTime())));
        carResponseDTO.setName((String) carData[4]);

        return carResponseDTO;
    }

    @Override
    public PageResponse<?> searchCarWithProcedure(SearchCarRequestDTO searchCarRequestDTO) {
        StoredProcedureQuery searchCarWithProcedure = entityManager.createStoredProcedureQuery("CARPROCEDURE.SEARCH_CAR");
        searchCarWithProcedure.registerStoredProcedureParameter("c_brand", String.class, ParameterMode.IN);
        searchCarWithProcedure.registerStoredProcedureParameter("c_color", String.class, ParameterMode.IN);
        searchCarWithProcedure.registerStoredProcedureParameter("c_name", String.class, ParameterMode.IN);
        searchCarWithProcedure.registerStoredProcedureParameter("c_start_date", Date.class, ParameterMode.IN);
        searchCarWithProcedure.registerStoredProcedureParameter("c_end_date", Date.class, ParameterMode.IN);
        searchCarWithProcedure.registerStoredProcedureParameter("c_page_no", Integer.class, ParameterMode.IN);
        searchCarWithProcedure.registerStoredProcedureParameter("c_page_size", Integer.class, ParameterMode.IN);
        searchCarWithProcedure.registerStoredProcedureParameter("c_total", Integer.class, ParameterMode.OUT);
        searchCarWithProcedure.registerStoredProcedureParameter("c_out_cursor", void.class, ParameterMode.REF_CURSOR);

        searchCarWithProcedure.setParameter("c_brand", searchCarRequestDTO.getBrand());
        searchCarWithProcedure.setParameter("c_color", searchCarRequestDTO.getColor());
        searchCarWithProcedure.setParameter("c_name", searchCarRequestDTO.getName());
        searchCarWithProcedure.setParameter("c_start_date", searchCarRequestDTO.getStartDate());
        searchCarWithProcedure.setParameter("c_end_date", searchCarRequestDTO.getEndDate());
        searchCarWithProcedure.setParameter("c_page_no", searchCarRequestDTO.getPageNo());
        searchCarWithProcedure.setParameter("c_page_size", searchCarRequestDTO.getPageSize());

        searchCarWithProcedure.execute();

        int totals = (Integer) searchCarWithProcedure.getOutputParameterValue("c_total");
        int totalPages = (int) Math.ceil((double) totals / searchCarRequestDTO.getPageSize());
        if (totals == 0){
            throw new AppException("Totals elements is zero");
        }

        List<Object[]> carList = searchCarWithProcedure.getResultList();

        List<CarResponseDTO> carResponseDTOList = new ArrayList<>();
        for (Object[] carData : carList){
            CarResponseDTO carResponseDTO = new CarResponseDTO();
            carResponseDTO.setId((Integer) carData[0]);
            carResponseDTO.setBrand((String) carData[1]);
            carResponseDTO.setColor((String) carData[2]);
            Timestamp dateBooking = (Timestamp) carData[3];
            carResponseDTO.setDateBooking(String.valueOf(new Date(dateBooking.getTime())));
            carResponseDTO.setName((String) carData[4]);
            carResponseDTOList.add(carResponseDTO);
        }

        if (carList.isEmpty()){
            throw new AppException("List car not found");
        }

        return PageResponse.builder()
                .pageNo(searchCarRequestDTO.getPageNo())
                .pageSize(searchCarRequestDTO.getPageSize())
                .totalElements(totals)
                .totalPages(totalPages)
                .items(carResponseDTOList)
                .build();
    }

    @Override
    public String deleteCarWithProcedure(CarRequestDTO carRequestDTO) {
        StoredProcedureQuery deleteCarWithProcedure = entityManager.createStoredProcedureQuery("CARPROCEDURE.DELETE_CAR");
        deleteCarWithProcedure.registerStoredProcedureParameter("c_id", Integer.class, ParameterMode.IN);

        deleteCarWithProcedure.setParameter("c_id", carRequestDTO.getId());
        deleteCarWithProcedure.execute();
        return "delete car successfully";
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<?> getAllCar(int pageNo, int pageSize, String sortBy) {
        int page_sub = 0;
        if (pageNo > 0){
            page_sub = pageNo - 1;
        }
        List<Sort.Order> orders = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()){
                if (matcher.group(3).equalsIgnoreCase("asc")){
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(page_sub, pageSize, Sort.by(orders));
        Page<Car> cars = carRepository.findAll(pageable);
        List<CarResponseDTO> carResponseDTOList = cars.stream().map(carMapper::toCarResponseDTO).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(cars.getTotalElements())
                .totalPages(cars.getTotalPages())
                .items(carResponseDTOList)
                .build();
    }

    @Override
    public CarResponseDTO updateCar(CarRequestDTO carRequestDTO) {
        Car findCar = getCarById(carRequestDTO.getId());
        carMapper.updateCar(findCar, carRequestDTO);
        carRepository.save(findCar);
        return carMapper.toCarResponseDTO(findCar);
    }

    @Override
    public CarResponseDTO updateCarWithProcedure(CarRequestDTO carRequestDTO) {
        Car car = carMapper.toCarEntity(carRequestDTO);
        StoredProcedureQuery updateCarWithProcedure = entityManager.createStoredProcedureQuery("CARPROCEDURE.UPDATE_CAR");
        updateCarWithProcedure.registerStoredProcedureParameter("c_id", Integer.class, ParameterMode.IN);
        updateCarWithProcedure.registerStoredProcedureParameter("c_brand", String.class, ParameterMode.IN);
        updateCarWithProcedure.registerStoredProcedureParameter("c_name", String.class, ParameterMode.IN);
        updateCarWithProcedure.registerStoredProcedureParameter("c_color", String.class, ParameterMode.IN);
        updateCarWithProcedure.registerStoredProcedureParameter("c_date_booking", Date.class, ParameterMode.IN);

        updateCarWithProcedure.setParameter("c_id", carRequestDTO.getId());
        updateCarWithProcedure.setParameter("c_brand", carRequestDTO.getBrand());
        updateCarWithProcedure.setParameter("c_name", carRequestDTO.getName());
        updateCarWithProcedure.setParameter("c_color", carRequestDTO.getColor());
        updateCarWithProcedure.setParameter("c_date_booking", carRequestDTO.getDateBooking());

        updateCarWithProcedure.execute();
        car.setId(carRequestDTO.getId());
        return carMapper.toCarResponseDTO(car);
    }

    @Override
    public String deleteCar(CarRequestDTO carRequestDTO) {
        carRepository.deleteById(carRequestDTO.getId());
        return "Delete car successfully";
    }
    public Car getCarById(Integer id){
        Optional<Car> findCar = carRepository.findById(id);
        if (findCar.isEmpty()) throw new AppException("Car not found");
        return findCar.get();
    }
}
