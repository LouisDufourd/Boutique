<#macro navbar>
<nav class="navbar navbar-expand-lg bg-light navbar-light">
    <div class="container">
        <a class="navbar-brand link-dark" href="/">
            <img src="/assets/img/cart.png" alt="logo" height="40" width="50">
            <span class="navbar-brand mb-0 h1">Shop</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navmenu">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navmenu">
            <ul class="navbar-nav ms-auto">
                <li>
                    <a class="nav-link link-secondary" href="/shops">
                        Shops
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link link-success" href="#" data-bs-toggle="modal" data-bs-target="#loginForm">
                        <div class="text-success">Login</div>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>
</#macro>