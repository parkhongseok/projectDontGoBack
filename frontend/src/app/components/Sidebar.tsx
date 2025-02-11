'use client'

// components/SideBar.js
import styles from './SideBar.module.css';
import { Nav, Image } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

export default function SideBar(){
  return (
    <div className={styles.sidebar}>
      {/* 로고 */}
      <div className={styles.logo}>
      <Nav.Link href="/">
        <Image src="/logo.svg" alt="Logo" className={styles.logoImage} />
      </Nav.Link>
      </div>
      {/* 네비게이션 메뉴 */}
      <div className={styles.navContainer}>
        <Nav defaultActiveKey="/" className="flex-column">
          <Nav.Link href="#profile">
            <Image src="/profile.svg" alt="profile" className={styles.navImage} />
          </Nav.Link>
          <Nav.Link href="#write">
            <Image src="/plus.svg" alt="Write" className={styles.navImage} />
          </Nav.Link>
          <Nav.Link href="test">
            <Image src="/like.svg" alt="Likes" className={styles.navImage} />
          </Nav.Link>
        </Nav>
      </div>

      {/* 설정 버튼 */}
    <div className='settingsContainer'>
        <Nav.Link href="#profile">
          <Image src="/setting.svg" alt="setting" className={styles.settingImage} />
        </Nav.Link>
    </div>

    </div>
  )
}


