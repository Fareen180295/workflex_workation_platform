# Multi-Tenant Resource Management System

A comprehensive backend system for managing multiple organizations (tenants) with their own resources and users, implementing secure tenant isolation using a schema-per-tenant approach.

## Overview

This system provides a scalable, secure multi-tenant backend solution with the following key features:

- **Multi-Tenancy**: Complete tenant isolation using schema-per-tenant approach
- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **Resource Management**: Full CRUD operations for tenant-specific resources
- **User Management**: Hierarchical user roles with appropriate permissions
- **Audit Logging**: Comprehensive activity tracking for compliance
- **Soft Deletes**: Data recovery capabilities for users and resources
- **Search & Filtering**: Advanced resource search with pagination
- **Performance Optimization**: Database indexing and connection pooling

## Architecture

### Technology Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL with Hibernate/JPA
- **Security**: Spring Security with JWT
- **Cache**: Redis for session management
- **Build Tool**: Maven
- **Java Version**: 17

### Database Schema

#### Multi-Tenant Architecture
- **Public Schema**: Contains tenant metadata and super admin operations
- **Tenant Schemas**: Each tenant has its own isolated schema for data segregation

#### Core Entities

1. **Tenant** (Public Schema)
   - `id`: Primary key
   - `name`: Unique tenant name
   - `schemaName`: Database schema identifier
   - `maxUsers`: User limit (default: 50)
   - `maxResources`: Resource limit (default: 500)
   - `contactEmail`: Tenant contact
   - `isActive`: Activation status

2. **User** (Tenant Schema)
   - `id`: Primary key
   - `username`: Unique within tenant
   - `password`: Encrypted password
   - `role`: ADMIN, MANAGER, or EMPLOYEE
   - `tenantId`: Foreign key to tenant
   - Soft delete support

3. **Resource** (Tenant Schema)
   - `id`: Primary key
   - `name`: Resource name
   - `description`: Resource description
   - `ownerId`: Foreign key to user
   - `tenantId`: Foreign key to tenant
   - Soft delete support

4. **AuditLog** (Tenant Schema)
   - `id`: Primary key
   - `userId`: Foreign key to user
   - `action`: Action performed
   - `details`: Additional information
   - `timestamp`: When action occurred
   - `tenantId`: Foreign key to tenant

## User Roles & Permissions

### Role Hierarchy

1. **SUPER_ADMIN** (System Level)
   - Manage tenants (create, update, delete)
   - View system-wide statistics
   - Access all tenant operations

2. **ADMIN** (Tenant Level)
   - Manage users within tenant
   - Manage resources within tenant
   - View audit logs
   - Full tenant administration

3. **MANAGER** (Tenant Level)
   - Manage resources within tenant
   - View resources
   - Cannot manage users

4. **EMPLOYEE** (Tenant Level)
   - View resources only
   - Read-only access to tenant resources

### Business Rules

- Maximum 50 users per tenant
- Maximum 500 resources per tenant
- Maximum 10 resources per user
- Complete tenant isolation enforced at all levels
- All create, update, delete operations are logged

## API Endpoints

### Authentication

```http
POST /auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password",
  "tenantId": "1"
}
```

```http
POST /auth/logout
Authorization: Bearer <token>
```

### Tenant Management (Super Admin Only)

```http
# Create tenant
POST /tenants
Authorization: Bearer <super-admin-token>
Content-Type: application/json

{
  "name": "Acme Corp",
  "schemaName": "acme_corp",
  "maxUsers": 100,
  "maxResources": 1000,
  "contactEmail": "admin@acme.com"
}

# Get tenant
GET /tenants/{id}
Authorization: Bearer <super-admin-token>

# Delete tenant
DELETE /tenants/{id}
Authorization: Bearer <super-admin-token>

# Get tenant statistics
GET /tenants/{id}/stats
Authorization: Bearer <super-admin-token>
```

### User Management (Admin Only)

```http
# Create user
POST /users
Authorization: Bearer <admin-token>
X-Tenant-ID: 1
Content-Type: application/json

{
  "username": "john.doe",
  "password": "securePassword",
  "role": "MANAGER",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@acme.com"
}

# Get users
GET /users?page=0&size=10
Authorization: Bearer <admin-token>
X-Tenant-ID: 1

# Delete user
DELETE /users/{id}
Authorization: Bearer <admin-token>
X-Tenant-ID: 1
```

### Resource Management

```http
# Create resource (Admin/Manager)
POST /resources
Authorization: Bearer <token>
X-Tenant-ID: 1
Content-Type: application/json

{
  "name": "Project Alpha",
  "description": "Strategic project for Q4"
}

# Get resources (All roles)
GET /resources?page=0&size=10
Authorization: Bearer <token>
X-Tenant-ID: 1

# Update resource (Admin/Manager/Owner)
PUT /resources/{id}
Authorization: Bearer <token>
X-Tenant-ID: 1
Content-Type: application/json

{
  "name": "Project Alpha Updated",
  "description": "Updated description"
}

# Delete resource (Admin/Manager/Owner)
DELETE /resources/{id}
Authorization: Bearer <token>
X-Tenant-ID: 1

# Search resources
GET /resources/search?name=Alpha&ownerId=1&page=0&size=10
Authorization: Bearer <token>
X-Tenant-ID: 1
```

### Audit Logs (Admin Only)

```http
# Get audit logs
GET /audit-logs?page=0&size=20
Authorization: Bearer <admin-token>
X-Tenant-ID: 1

# Get audit logs by user
GET /audit-logs/user/{userId}?page=0&size=20
Authorization: Bearer <admin-token>
X-Tenant-ID: 1

# Get audit logs by action
GET /audit-logs/action/{action}?page=0&size=20
Authorization: Bearer <admin-token>
X-Tenant-ID: 1
```

## Configuration

### Database Configuration

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/multi_tenant_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Hibernate Multi-tenancy
spring.jpa.properties.hibernate.multitenancy=SCHEMA
spring.jpa.properties.hibernate.tenant_identifier_resolver=com.workflex.workation.config.TenantIdentifierResolver
spring.jpa.properties.hibernate.multi_tenant_connection_provider=com.workflex.workation.config.MultiTenantConnectionProvider
```

### JWT Configuration

```properties
# JWT Settings
jwt.secret=mySecretKey1234567890abcdefghijklmnopqrstuvwxyz1234567890
jwt.expiration=86400000
```

### Redis Configuration

```properties
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
spring.data.redis.password=
spring.data.redis.timeout=2000ms
```

## Security Features

### Multi-Tenant Isolation

1. **Schema-level Isolation**: Each tenant operates in a separate database schema
2. **Connection-level Security**: Dynamic schema switching based on tenant context
3. **Request-level Validation**: Tenant ID validation on every request
4. **Data Access Controls**: Repository-level tenant filtering

### Authentication & Authorization

1. **JWT Tokens**: Stateless authentication with tenant-aware claims
2. **Role-based Access**: Method-level security with Spring Security
3. **Password Security**: BCrypt encryption for user passwords
4. **Session Management**: Redis-based session storage and blacklist

### Audit & Compliance

1. **Activity Logging**: All CRUD operations are automatically logged
2. **User Tracking**: Login/logout events with timestamps
3. **Data Retention**: Soft deletes with recovery capabilities
4. **Compliance Ready**: Audit trails for regulatory requirements

## Performance Optimizations

### Database Optimizations

1. **Indexing Strategy**:
   - Tenant-specific indexes on all multi-tenant tables
   - Composite indexes for common query patterns
   - Optimized indexes for search functionality

2. **Connection Management**:
   - Connection pooling with HikariCP
   - Schema-aware connection provider
   - Efficient connection reuse

### Caching Strategy

1. **Redis Integration**:
   - JWT token blacklist
   - Session management
   - Frequently accessed tenant metadata

2. **Application-level Caching**:
   - Tenant configuration caching
   - User permissions caching

## Deployment

### Prerequisites

1. **Java 17+**
2. **PostgreSQL 12+**
3. **Redis 6+**
4. **Maven 3.8+**

### Build & Run

```bash
# Clone the repository
git clone <repository-url>
cd workation

# Build the application
./mvnw clean package

# Run with development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run with production profile
java -jar target/workation-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Database Setup

```sql
-- Create main database
CREATE DATABASE multi_tenant_db;

-- Create initial tenant schema (example)
CREATE SCHEMA tenant_1;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE multi_tenant_db TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA tenant_1 TO postgres;
```

### Environment Variables

```bash
# Database
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=multi_tenant_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

# JWT
export JWT_SECRET=your-secret-key-here
export JWT_EXPIRATION=86400000

# Redis
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
```

## Testing

### Unit Tests

```bash
# Run unit tests
./mvnw test
```

### Integration Tests

```bash
# Run integration tests
./mvnw test -Pintegration-tests
```

### API Testing

Use the provided Postman collection or test with curl:

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password",
    "tenantId": "1"
  }'

# Create resource
curl -X POST http://localhost:8080/resources \
  -H "Authorization: Bearer <token>" \
  -H "X-Tenant-ID: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Resource",
    "description": "A test resource"
  }'
```

## Monitoring & Logging

### Application Logs

```properties
# Logging configuration
logging.level.com.workflex.workation=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Health Checks

- **Actuator Endpoints**: `/actuator/health`, `/actuator/metrics`
- **Database Health**: Automatic connection monitoring
- **Redis Health**: Cache connectivity monitoring

## Advanced Features

### Rate Limiting

```properties
# Rate limiting configuration
app.rate-limit.enabled=true
app.rate-limit.requests-per-minute=100
app.rate-limit.burst-capacity=20
```

### Batch Processing

- **Bulk user creation**: CSV import functionality
- **Batch resource operations**: Multi-resource management
- **Data migration tools**: Tenant data import/export

### Analytics & Reporting

- **Usage statistics**: Per-tenant resource utilization
- **User activity reports**: Login patterns and usage metrics
- **System performance metrics**: Response times and throughput

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:

- **Email**: support@workflex.com
- **Documentation**: [Wiki](https://github.com/workflex/workation/wiki)
- **Issues**: [GitHub Issues](https://github.com/workflex/workation/issues)

## Roadmap

### Upcoming Features

- [ ] GraphQL API support
- [ ] Real-time notifications
- [ ] Advanced analytics dashboard
- [ ] Mobile API optimizations
- [ ] Kubernetes deployment configs
- [ ] Database sharding support
- [ ] Multi-region deployment

### Version History

- **v1.0.0**: Initial release with core multi-tenancy features
- **v1.1.0**: Added advanced search and filtering
- **v1.2.0**: Performance optimizations and caching
- **v2.0.0**: Enhanced security and audit features (planned)