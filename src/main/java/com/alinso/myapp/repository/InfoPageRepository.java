package com.alinso.myapp.repository;
import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.InfoPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InfoPageRepository extends JpaRepository<InfoPage,Long> {


}
