import { Logo } from './Logo'
import { Icon } from './Icon'

export function PublicHeader() {
  return <header className="site-header">
    <div className="container site-header__inner">
      <Logo />
      <nav className="nav-links">
        <a href="#motor">Motor Insurance</a>
        <a href="#how-it-works">How it works</a>
        <div className="contact-menu">
          <button className="nav-button" type="button">Contact us</button>
          <div className="contact-menu__dropdown">
            <a href="tel:+254713559966"><span className="contact-menu__icon">☎</span><span><strong>Call us</strong><small>+254 713 559 966</small></span><Icon name="arrow" /></a>
            <a href="mailto:twirextras@gmail.com"><span className="contact-menu__icon">✉</span><span><strong>Email us</strong><small>twirextras@gmail.com</small></span><Icon name="arrow" /></a>
          </div>
        </div>
      </nav>
      <div className="header-actions"><a className="button button--ghost" href="/login">Sign in</a><a className="button button--primary" href="/register">Get started</a></div>
    </div>
  </header>
}
