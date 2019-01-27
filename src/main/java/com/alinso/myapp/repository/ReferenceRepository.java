//package com.alinso.myapp.repository;
//
//import com.alinso.myapp.entity.Reference;
//import com.alinso.myapp.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ReferenceRepository extends JpaRepository<Reference,Long> {
//
//
//    @Query("select reference from Reference reference where reference.parent=:parent")
//    List<Reference> findByParent(@Param("parent") User loggedUser);
//
//    @Query("select reference from Reference reference where reference.referenceCode=:code")
//    Reference findByCode(@Param("code") String referenceCode);
//}
