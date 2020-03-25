package com.alinso.myapp.repository;


import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.Vibe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VibeRepository extends JpaRepository<Vibe,Long> {


    @Query("select v from Vibe v where v.reader=:reader and v.writer=:writer and v.deleted=0")
    Vibe findByWriterAndReader(@Param("writer") User writer, @Param("reader")User reader);


    @Query("select v from Vibe v where v.reader= :reader")
    List<Vibe>  findByReader(@Param("reader") User reader);

    @Query("select v from Vibe v where v.writer= :writer")
    List<Vibe> findByWriter(@Param("writer") User writer);

    @Query("select v from Vibe v where v.reader= :reader and v.deleted=0")
    List<Vibe> findByReaderNonDeleted(User reader);

    @Query("select v from Vibe v where v.writer= :writer and v.deleted=1")
    List<Vibe> findByWriterOnlyDeleted(@Param("writer")User writer);

    @Query("select v from Vibe v where v.reader= :reader and v.deleted=1")
    List<Vibe> findByReaderOnlyDeleted(@Param("reader") User reader);
}
