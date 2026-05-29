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
        {/* Library icon inspired by stacked books */}
        <div className={styles['logo-mark']}>
          <div className={styles['book']} style={{ '--book-index': 0 } as React.CSSProperties}></div>
          <div className={styles['book']} style={{ '--book-index': 1 } as React.CSSProperties}></div>
          <div className={styles['book']} style={{ '--book-index': 2 } as React.CSSProperties}></div>
        </div>
      </div>
      <div className={styles['logo-text']}>
        <h1>CLASS</h1>
        <p>Computer Library Access System</p>
      </div>
    </div>
  );
};

export default Logo;
