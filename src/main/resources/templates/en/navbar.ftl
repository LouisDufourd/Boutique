<#macro navbar>
    <nav class="navbar navbar-expand-lg bg-light navbar-light">
        <div class="container">
            <!--Logo Accueil-->
            <a class="navbar-brand link-dark" href="/">
                <img src="/assets/img/cart.png" alt="logo" height="40" width="50">
                <span class="navbar-brand mb-0 h1">Shop</span>
            </a>

            <!--Hamburger menu-->
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navmenu">
                <span class="navbar-toggler-icon"></span>
            </button>

            <!--Navbar Menu-->
            <div class="collapse navbar-collapse" id="navmenu">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link link-secondary" href="/profile">
                            Profile
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link link-secondary" href="#" data-bs-toggle="modal" data-bs-target="#createShopForm">
                            Create a store
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link link-secondary" href="/stores">
                            My stores
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link link-danger" href="/disconnect">
                            <div class="text-danger">Disconnect</div>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <br/>
</#macro>