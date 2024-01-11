package com.belrose.emailotpverification.repository;

import com.belrose.emailotpverification.model.Users;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UsersRepository extends ReactiveMongoRepository<Users,String> {
    Mono<Users> findByEmail(String email);
}
