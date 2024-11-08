package com.example.jpa_dynamic_query.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarSearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {

    private Predicate predicate;
    private CriteriaBuilder criteriaBuilder;
    private Root root;
    @Override
    public void accept(SearchCriteria param) {
//        if (param.getOperation().equalsIgnoreCase(">")){
//            predicate = criteriaBuilder.and(predicate, criteriaBuilder
//                    .greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
//        } else if (param.getOperation().equalsIgnoreCase("<")){
//            predicate = criteriaBuilder.and(predicate, criteriaBuilder
//                    .lessThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
//        } else if (param.getOperation().equalsIgnoreCase(":")){
//            if (root.get(param.getKey()).getJavaType() == String.class) {
//                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
//                        root.get(param.getKey()), "%" + param.getValue() + "%"));
//            } else {
//                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
//                        root.get(param.getKey()), param.getValue()));
//            }
//        }
        if (root.get(param.getKey()).getJavaType() == String.class) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
                    root.get(param.getKey()), "%" + param.getValue() + "%"));
        } else {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                    root.get(param.getKey()), param.getValue()));
        }
    }
}
