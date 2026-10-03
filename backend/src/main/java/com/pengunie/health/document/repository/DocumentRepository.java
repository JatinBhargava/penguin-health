package com.pengunie.health.document.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pengunie.health.document.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
}