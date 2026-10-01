-- chinook_local.db
-- Script de creación de la base local SQLite (frontend Java)

CREATE TABLE IF NOT EXISTS invoice_line (
  invoice_line_id INTEGER PRIMARY KEY AUTOINCREMENT,
  invoice_id INTEGER NOT NULL,
  track_id INTEGER NOT NULL,
  unit_price REAL NOT NULL,
  quantity INTEGER NOT NULL
);

-- Tabla temporal para cabeceras en modo degradado
CREATE TABLE IF NOT EXISTS invoice_temp (
  invoice_id INTEGER PRIMARY KEY,
  customer_id INTEGER NOT NULL,
  invoice_date TEXT NOT NULL,
  billing_address TEXT,
  billing_city TEXT,
  billing_state TEXT,
  billing_country TEXT,
  billing_postal_code TEXT,
  total REAL NOT NULL,
  sync_status TEXT DEFAULT 'PENDING', -- PENDING | SYNCED
  created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Cola de sincronización
CREATE TABLE IF NOT EXISTS sync_queue (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  invoice_id INTEGER NOT NULL,
  payload TEXT NOT NULL,
  status TEXT DEFAULT 'PENDING',
  attempts INTEGER DEFAULT 0,
  last_attempt TEXT
);
