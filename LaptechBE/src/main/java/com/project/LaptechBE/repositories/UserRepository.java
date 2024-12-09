package com.project.LaptechBE.repositories;

import com.project.LaptechBE.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    public Boolean existsByEmail(String email);
    public User findByEmail(String email);
    public User getById(String id);
    public boolean existsById(String id);

    public Optional<User> findById(ObjectId objectId);
    public Optional<User> deleteById(ObjectId objectId);
}
