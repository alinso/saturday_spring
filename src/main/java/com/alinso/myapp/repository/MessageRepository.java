package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Message;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {


    @Query("select message from Message message where" +
            " (message.reader =:user1 and writer=:user2)" +
            " or (message.reader =:user2 and writer=:user1)" +
            " order by message.id asc")
    List<Message> getByReaderWriter(@Param("user1") User user1, @Param("user2")User user2);




    @Query(value = "select o from Message o left join Message b " +
            "on (o.reader = b.reader and o.writer = b.writer and o.createdAt<b.createdAt) " +
            "where b.createdAt is null and (o.reader=:me or o.writer=:me) order by o.createdAt desc")
    List<Message> latestMessageFromEachConversation(@Param("me") User me);

}
