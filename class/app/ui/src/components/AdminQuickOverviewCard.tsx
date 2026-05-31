import React from 'react';
import styles from '../styles/adminQuickOverviewCard.module.css';

interface ActionButton {
  label: string;
  icon: string;
  color: 'success' | 'danger';
  onClick: () => void;
}

export interface QuickOverviewCardProps {
  id: number;
  title: string;
  details: Array<{ label: string; value: string }>;
  actions: ActionButton[];
  onClick?: () => void;
  highlighted?: boolean;
}

const AdminQuickOverviewCard: React.FC<QuickOverviewCardProps> = ({
  id,
  title,
  details,
  actions,
  onClick,
  highlighted = false,
}) => {
  const getColorClass = (color: string) => {
    switch (color) {
      case 'success':
        return styles['btn-success'];
      case 'danger':
        return styles['btn-danger'];
      default:
        return styles['btn-neutral'];
    }
  };

  return (
    <div
      className={`${styles['card']} ${highlighted ? styles['highlighted'] : ''}`}
      onClick={onClick}
      role="button"
      tabIndex={0}
    >
      {/* Header */}
      <div className={styles['card-header']}>
        <h3 className={styles['title']}>{title}</h3>
        <span className={styles['id']}>#{id}</span>
      </div>

      {/* Details */}
      <div className={styles['details']}>
        {details.map((detail, idx) => (
          <div key={idx} className={styles['detail-row']}>
            <span className={styles['label']}>{detail.label}:</span>
            <span className={styles['value']}>{detail.value}</span>
          </div>
        ))}
      </div>

      {/* Action Buttons */}
      <div className={styles['actions']}>
        {actions.map((action, idx) => (
          <button
            key={idx}
            className={`${styles['action-btn']} ${getColorClass(action.color)}`}
            onClick={(e) => {
              e.stopPropagation();
              action.onClick();
            }}
          >
            {action.icon} {action.label}
          </button>
        ))}
      </div>
    </div>
  );
};

export default AdminQuickOverviewCard;
