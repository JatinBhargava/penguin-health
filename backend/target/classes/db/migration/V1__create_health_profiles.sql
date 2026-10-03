CREATE TABLE health_profiles (
    id UUID PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL UNIQUE,

    date_of_birth DATE,
    gender VARCHAR(30),

    height_cm NUMERIC(5,2),
    weight_kg NUMERIC(5,2),

    blood_group VARCHAR(10),

    allergies TEXT,
    existing_conditions TEXT,
    medications TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);