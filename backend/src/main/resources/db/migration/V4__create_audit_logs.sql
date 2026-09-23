CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    action VARCHAR(40) NOT NULL,
    old_value VARCHAR(1000),
    new_value VARCHAR(1000),
    changed_by UUID NOT NULL,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_audit_logs_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_audit_logs_changed_by FOREIGN KEY (changed_by) REFERENCES users (id),
    CONSTRAINT ck_audit_logs_action CHECK (action IN ('SALARY_CREATED', 'SALARY_UPDATED', 'EMPLOYEE_UPDATED', 'EMPLOYEE_DEACTIVATED'))
);

CREATE INDEX idx_audit_logs_employee_changed_at ON audit_logs (employee_id, changed_at DESC);
CREATE INDEX idx_audit_logs_changed_at ON audit_logs (changed_at);
