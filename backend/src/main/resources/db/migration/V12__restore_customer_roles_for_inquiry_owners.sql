-- Restore the CUSTOMER role only for legacy accounts that already own an
-- insurance inquiry. That ownership is evidence that the account is a
-- customer portal account, while unassigned staff and unrelated accounts are
-- left unchanged.
INSERT INTO user_roles (user_id, role_id)
SELECT DISTINCT inquiry.customer_id, roles.id
FROM insurance_inquiries inquiry
CROSS JOIN roles
WHERE roles.name = 'CUSTOMER'
  AND NOT EXISTS (
      SELECT 1
      FROM user_roles
      WHERE user_roles.user_id = inquiry.customer_id
  )
ON CONFLICT (user_id, role_id) DO NOTHING;
