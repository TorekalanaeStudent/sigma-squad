import React, { useState } from 'react';
import styles from '../styles/sidebarNav.module.css';

interface SidebarNavProps {
  activeTab: 'overview' | 'computers' | 'pending' | 'history';
  onTabChange: (tab: 'overview' | 'computers' | 'pending' | 'history') => void;
}

const SidebarNav: React.FC<SidebarNavProps> = ({ activeTab, onTabChange }) => {
  const [isOpen, setIsOpen] = useState(true);

  const menuItems = [
    { id: 'overview', label: 'Overview', icon: '📊' },
    { id: 'computers', label: 'Computers', icon: '💻' },
    { id: 'pending', label: 'Pending', icon: '⏳' },
    { id: 'history', label: 'History', icon: '📚' },
  ];

  return (
    <>
      {/* Mobile Toggle Button */}
      <button
        className={styles['toggle-btn']}
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Toggle sidebar"
      >
        ☰
      </button>

      {/* Sidebar */}
      <nav className={`${styles['sidebar']} ${isOpen ? styles['open'] : styles['closed']}`}>
        <div className={styles['sidebar-header']}>
          <h2>Menu</h2>
          <button
            className={styles['close-btn']}
            onClick={() => setIsOpen(false)}
            aria-label="Close sidebar"
          >
            ✕
          </button>
        </div>

        <div className={styles['menu-items']}>
          {menuItems.map((item) => (
            <button
              key={item.id}
              className={`${styles['menu-item']} ${activeTab === item.id ? styles['active'] : ''}`}
              onClick={() => {
                onTabChange(item.id as 'overview' | 'computers' | 'pending' | 'history');
                // Close sidebar on mobile after selection
                if (window.innerWidth < 768) {
                  setIsOpen(false);
                }
              }}
            >
              <span className={styles['label']}>{item.label}</span>
            </button>
          ))}
        </div>

        <div className={styles['sidebar-footer']}>
          <p>CLASS System v1.0</p>
        </div>
      </nav>

      {/* Overlay for mobile */}
      {isOpen && (
        <div
          className={styles['overlay']}
          onClick={() => setIsOpen(false)}
          aria-hidden="true"
        />
      )}
    </>
  );
};

export default SidebarNav;
