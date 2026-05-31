import React from 'react';
import styles from '../styles/logo.module.css';

interface LogoProps {
  size?: 'small' | 'medium' | 'large';
}

export const Logo: React.FC<LogoProps> = ({ size = 'medium' }) => {
  const sizeClass = styles[`logo-${size}`];

  return (
    <div className={`${styles.logo} ${sizeClass}`}>
      <div className={styles['logo-container']}>
        <img 
          src="/logo.svg" 
          alt="CLASS Logo" 
          className={styles['logo-image']}
        />
      </div>
      <div className={styles['logo-text']}>
        <h1>CLASS</h1>
        <p>Computer Library Access System</p>
      </div>
    </div>
  );
};

export default Logo;
