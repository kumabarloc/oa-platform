-- V3: 工作流扩展 - 加签/减签/驳回/退回功能支持
-- 1. 收文表添加流程字段
ALTER TABLE sys_incoming_document ADD COLUMN IF NOT EXISTS process_instance_id VARCHAR(64) DEFAULT NULL COMMENT 'Flowable流程实例ID';
ALTER TABLE sys_incoming_document ADD COLUMN IF NOT EXISTS current_step VARCHAR(64) DEFAULT NULL COMMENT '当前审批步骤';

-- 2. 公文历史记录表（支持发文+收文审批历史）
CREATE TABLE IF NOT EXISTS sys_document_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    doc_id          BIGINT NOT NULL COMMENT '公文/收文ID',
    doc_type        VARCHAR(20) DEFAULT 'publish' COMMENT 'publish-发文, incoming-收文',
    user_id         BIGINT COMMENT '操作用户ID',
    user_name       VARCHAR(64) COMMENT '操作用户姓名',
    action          VARCHAR(32) NOT NULL COMMENT 'approve-通过, reject-驳回, return-退回, add_sign-加签, remove_sign-减签',
    reason          VARCHAR(500) COMMENT '原因/意见',
    task_id         VARCHAR(64) COMMENT '关联任务ID',
    task_name       VARCHAR(128) COMMENT '任务名称',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_doc (doc_id, doc_type),
    INDEX idx_user (user_id),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公文审批历史记录表';
