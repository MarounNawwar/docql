-- ── doc_packages ────────────────────────────────────────────────────────────
CREATE TABLE doc_packages
(
    id              VARCHAR(255)             NOT NULL PRIMARY KEY,
    team            VARCHAR(255)             NOT NULL,
    product         VARCHAR(255)             NOT NULL,
    version         VARCHAR(255)             NOT NULL,
    published_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    index_file_path VARCHAR(1000)            NOT NULL
);

CREATE INDEX idx_doc_packages_team    ON doc_packages (team);
CREATE INDEX idx_doc_packages_product ON doc_packages (team, product);
CREATE UNIQUE INDEX idx_doc_packages_team_product_version
    ON doc_packages (team, product, version);

-- ── doc_package_tags (ElementCollection join table) ──────────────────────────
CREATE TABLE doc_package_tags
(
    package_id VARCHAR(255) NOT NULL REFERENCES doc_packages (id) ON DELETE CASCADE,
    tag        VARCHAR(255) NOT NULL
);

CREATE INDEX idx_doc_package_tags_tag ON doc_package_tags (tag);

-- ── doc_files ────────────────────────────────────────────────────────────────
CREATE TABLE doc_files
(
    package_id VARCHAR(255)  NOT NULL REFERENCES doc_packages (id) ON DELETE CASCADE,
    path       VARCHAR(1000) NOT NULL,
    title      VARCHAR(1000),
    content    TEXT,
    PRIMARY KEY (package_id, path)
);

CREATE INDEX idx_doc_files_package_id ON doc_files (package_id);

