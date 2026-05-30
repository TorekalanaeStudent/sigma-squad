import React, { useState } from 'react';
import styles from '../styles/sidebarNav.module.css';

interface AdminSidebarNavProps {
  activeTab: 'dashboard' | 'pending' | 'sessions' | 'extensions';
  onTabChange: (tab: 'dashboard' | 'pending' | 'sessions' | 'extensions') => void;
}

const AdminSidebarNav: React.FC<AdminSidebarNavProps> = ({ activeTab, onTabChange }) => {
  const [isOpen, setIsOpen] = useState(true);

  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: '📊' },
    { id: 'pending', label: 'Pending Reservations', icon: '📋' },
    { id: 'sessions', label: 'Active Sessions', icon: '🚀' },
    { id: 'extensions', label: 'Extensions', icon: '⏱️' },
  ];

  return (
    <>
      <button className={styles['toggle-btn']} onClick={() => setIsOpen(!isOpen)} aria-label="Toggle sidebar">
        ☰
      </button>

      <nav className={`${styles['sidebar']} ${isOpen ? styles['open'] : styles['closed']}`}>
        <div className={styles['sidebar-header']}>
          <h2>Menu</h2>
          <button className={styles['close-btn']} onClick={() => setIsOpen(false)} aria-label="Close sidebar">
            ✕
          </button>
        </div>

        <div className={styles['menu-items']}>
          {menuItems.map((item) => (
            <button
              key={item.id}
              className={`${styles['menu-item']} ${activeTab === item.id ? styles['active'] : ''}`}
              onClick={() => {
                onTabChange(item.id as 'dashboard' | 'pending' | 'sessions' | 'extensions');
                if (window.innerWidth < 768) setIsOpen(false);
              }}
            >
              <span className={styles['icon']}>{item.icon}</span>
              <span className={styles['label']}>{item.label}</span>
            </button>
          ))}
        </div>

        <div className={styles['sidebar-footer']}>
          <p>CLASS Admin v1.0</p>
        </div>
      </nav>

      {isOpen && <div className={styles['overlay']} onClick={() => setIsOpen(false)} aria-hidden="true" />}
    </>
  );
};

export default AdminSidebarNav;
