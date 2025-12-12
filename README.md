# Suprim CLI

Database tools manager for PostgreSQL and MySQL.

## Installation

### Quick Install (macOS/Linux)

```bash
curl -sL https://github.com/suprim-corp/suprim-cli/releases/latest/download/install.sh | bash
```

Requires Java 17+. After install, restart terminal or run `source ~/.zshrc`.

### Manual Install

1. Download JAR from [releases](https://github.com/suprim-corp/suprim-cli/releases)
2. Run: `java -jar suprim-cli.jar --help`

## Getting Started

```bash
# 1. Install the CLI
curl -sL https://github.com/suprim-corp/suprim-cli/releases/latest/download/install.sh | bash

# 2. Install the migration tool
suprim install migration

# 3. Create config file
cat > suprim.yml << 'EOF'
database:
  driver: postgresql
  url: jdbc:postgresql://localhost:5432/mydb
  username: postgres
  password: secret
migrations:
  path: migrations
EOF

# 4. Create your first migration
suprim make:migration create_users_table

# 5. Run migrations
suprim migrate
```

## Tool Management

Suprim CLI is a tool manager that downloads and manages database tools.

```bash
suprim install migration    # Install migration tool
suprim install license      # Install license tool (for commercial DBs)
suprim uninstall migration  # Uninstall a tool
suprim list                 # List installed tools
```

Tools are installed to `~/.suprim/tools/`.

## Migration Commands

Requires: `suprim install migration`

| Command | Description |
|---------|-------------|
| `suprim migrate` | Run pending migrations |
| `suprim rollback` | Rollback last batch |
| `suprim reset --force` | Rollback all migrations |
| `suprim status` | Show migration status |
| `suprim make:migration <name>` | Create new migration file |

## Configuration

Create `suprim.yml` in your project root:

```yaml
database:
  driver: postgresql  # postgresql, mysql, mysql8
  url: jdbc:postgresql://localhost:5432/mydb
  username: postgres
  password: secret

migrations:
  path: migrations
  table: suprim_migrations  # optional
```

## Migration File Format

```yaml
# migrations/2024_01_15_000001_create_users_table.yml
migration:
  name: create_users_table
  transaction: true

up:
  - create_table:
      name: users
      columns:
        - id: { type: bigserial, primary: true }
        - email: { type: varchar, length: 255, nullable: false, unique: true }
        - name: { type: varchar, length: 100 }
        - created_at: { type: timestamptz, default: CURRENT_TIMESTAMP }

down:
  - drop_table: users
```

## Supported Databases

### Free (Open Source)

- PostgreSQL (all versions)
- MySQL 5.7+, 8.0+
- MariaDB

### Commercial (Requires License)

- Oracle Database
- Microsoft SQL Server
- IBM DB2

For commercial licenses: `suprim license status`

## Requirements

- Java 17+

## License

MIT
