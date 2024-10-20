package com.distributed.miraeasset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseAccountRepository<T> extends JpaRepository<T, Integer> {

}
