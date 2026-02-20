--- feedback table
CREATE TABLE feedback (
      id BIGSERIAL PRIMARY KEY,

      public_id UUID NOT NULL UNIQUE,
      assignment_id UUID NOT NULL,

      given_by_actor_type VARCHAR(20) NOT NULL,
      given_by_actor_id UUID NOT NULL,

      target_actor_type VARCHAR(20) NOT NULL,
      target_actor_id UUID,

      rating INT CHECK (rating BETWEEN 1 AND 5),
      comment TEXT,

      created_at TIMESTAMP NOT NULL,

      CONSTRAINT chk_feedback_content
          CHECK (rating IS NOT NULL OR comment IS NOT NULL),

      CONSTRAINT uq_feedback_once
          UNIQUE (assignment_id, given_by_actor_type, target_actor_type)
);
