package com.alinso.myapp.repository;


import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {


    @Query("select v from Vote v where v.reader=:reader and v.writer=:writer and v.deleted=0")
    Vote findByWriterAndReader(@Param("writer") User writer, @Param("reader")User reader);


    @Query("select v from Vote v where v.reader= :reader")
    List<Vote>  findByReader(@Param("reader") User reader);

    @Query("select v from Vote v where v.writer= :writer")
    List<Vote> findByWriter(@Param("writer") User writer);

    @Query("select v from Vote v where v.reader= :reader and v.deleted=0")
    List<Vote> findByReaderNonDeleted(User reader);

    @Query("select v from Vote v where v.writer= :writer and v.deleted=1")
    List<Vote> findByWriterOnlyDeleted(@Param("writer")User writer);

    @Query("select v from Vote v where v.reader= :reader and v.deleted=1")
    List<Vote> findByReaderOnlyDeleted(@Param("reader") User reader);
}
