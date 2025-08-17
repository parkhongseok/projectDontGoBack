import styles from "./Badge.module.css";

// 뱃지의 종류를 타입으로 정의합니다.
type BadgeProps = {
  role: "me" | "admin" | "guest";
  children: React.ReactNode;
};

export default function Badge({ role, children }: BadgeProps) {
  // role 값에 따라 다른 CSS 클래스를 적용합니다. (e.g., styles.admin, styles.me)
  if (role == "admin" || role == "guest") {
    const roleClass = styles[role] || "";
    return <span className={`${styles.badge} ${roleClass}`}>{children}</span>;
  }
}
