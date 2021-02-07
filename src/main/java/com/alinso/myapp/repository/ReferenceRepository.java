package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Application;
import com.alinso.myapp.entity.Reference;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference,Long> {


    @Query("select reference from Reference reference where reference.parent=:parent and reference.parent.enabled=true")
    List<Reference> findByParent(@Param("parent") User loggedUser);

    @Query("select reference from Reference reference where reference.referenceCode=:code and reference.parent.enabled=true")
    Reference findByCode(@Param("code") String referenceCode);


    @Query("select r from Reference r where r.referenceCode=:code and reference.parent.enabled=true and r.application is null")
    Reference getValidReference(@Param("code") String code);

    @Query("select r from Reference r where r.child=:child and r.child.enabled=true" )
    Reference findByChild(@Param("child") User child);

    @Query("select reference from Reference reference where reference.referenceCode=:code and reference.parent.enabled=true")
    List<Reference> findReferencesByCode(@Param("code") String referenceCode);


    @Query("select count(r) from Reference r where r.referenceCode=:code")
    int findCountByReferenceCode(@Param("code") String code);

    @Query("select r from Reference r where r.application=:application and r.parent.enabled=:true")
    Reference getReferenceByApplication(@Param("application") Application application);
}
