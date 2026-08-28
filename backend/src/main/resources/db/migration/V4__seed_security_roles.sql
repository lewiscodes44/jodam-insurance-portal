INSERT INTO roles (
    name,
    description
)
VALUES
    (
        'ADMIN',
        'System administrator'
    ),
    (
        'AGENT',
        'Insurance agent'
    ),
    (
        'CUSTOMER',
        'Insurance customer'
    )
ON CONFLICT (name)
DO NOTHING;