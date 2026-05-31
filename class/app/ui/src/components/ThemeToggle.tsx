import React from 'react';
import { useTheme } from '../context/ThemeContext';
import styles from '../styles/themeToggle.module.css';

export const ThemeToggle: React.FC = () => {
  const { theme, toggleTheme } = useTheme();

  return (
    <button className={styles['theme-toggle']} onClick={toggleTheme} title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}>
      {theme === 'dark' ? '☀️' : '🌙'}
    </button>
  );
};

export default ThemeToggle;
