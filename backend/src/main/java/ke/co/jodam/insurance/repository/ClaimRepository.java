package ke.co.jodam.insurance.repository;

import ke.co.jodam.insurance.entity.Claim;
import ke.co.jodam.insurance.entity.ClaimStatus;
import ke.co.jodam.insurance.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository
        extends JpaRepository<Claim, Long> {

    Optional<Claim> findByClaimNumber(
            String claimNumber
    );

    List<Claim> findByPolicyOrderByCreatedAtDesc(
            ke.co.jodam.insurance.entity.Policy policy
    );

    List<Claim> findByPolicy_CustomerOrderByCreatedAtDesc(
            User customer
    );

    List<Claim> findByAssignedAgentOrderByCreatedAtDesc(
            User agent
    );

    List<Claim> findByStatusOrderByCreatedAtDesc(
            ClaimStatus status
    );

    List<Claim> findAllByOrderByCreatedAtDesc();

    long countByStatus(
            ClaimStatus status
    );
}