<#import "header.ftl" as head>
<!DOCTYPE html>
<html lang="fr">
<head>
    <@head.header>
    </@head.header>
    <title>Accueil</title>
</head>
<body>
<nav class="navbar navbar-expand-lg bg-light navbar-light">
    <div class="container">
        <a class="navbar-brand link-dark" href="/">
            <img src="/assets/img/cart.png" alt="logo" height="40" width="50">
            <span class="navbar-brand mb-0 h1">Boutique</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navmenu">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navmenu">
            <ul class="navbar-nav ms-auto">
                <#if data.connected>
                    <li class="nav-item">
                        <a class="nav-link link-secondary" href="/profile">
                            Profile
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link link-secondary" href="#" data-bs-toggle="modal"
                           data-bs-target="#createShopForm">
                            Créer une boutique
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link link-secondary" href="/stores">
                            Mes boutiques
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link link-danger" href="/disconnect">
                            <div class="text-danger">Déconnexion</div>
                        </a>
                    </li>
                <#else>
                    <li class="nav-item">
                        <a class="nav-link link-success" href="#" data-bs-toggle="modal" data-bs-target="#loginForm">
                            <div class="text-success">Connexion</div>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link link-secondary" href="#" data-bs-toggle="modal"
                           data-bs-target="#registerForm">
                            S'inscrire
                        </a>
                    </li>
                </#if>
            </ul>
        </div>
    </div>
</nav>
<#switch data.createshoperror>
    <#case 1>
        <div class="alert alert-danger" role="alert">
            Le nom de la boutique est déjà utilisé
        </div>
        <#break>
    <#case 2>
        <div class="alert alert-danger" role="alert">
            Le nom de la boutique est invalide
        </div>
        <#break>
</#switch>
<#if data.connexionerror>
    <div class="alert alert-danger" role="alert">
        Le nom d'utilisateur ou le mot de passe est incorrect
    </div>
</#if>
<#switch data.registererror>
    <#case 1>
        <div class="alert alert-danger" role="alert">
            Le nom d'utilisateur est déjà utiliser
        </div>
        <#break>
    <#case 2>
        <div class="alert alert-danger" role="alert">
            Le nom d'utilisateur est invalide
        </div>
        <#break>
    <#case 3>
        <div class="alert alert-danger" role="alert">
            Le mot de passe est invalide
        </div>
        <#break>
</#switch>
<#if !data.connected>
    <!-- Modal -->
    <div class="modal fade" id="loginForm" tabindex="-1" aria-labelledby="connexionLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="get" action="/connect">
                    <div class="modal-header">
                        <h5 class="modal-title" id="connexionLabel">Connexion</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">

                        <div class="mb-3">
                            <label for="username" class="form-label">Nom d'utilisateur</label>
                            <input type="text" class="form-control" name="username" id="username" minlength="4" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Mots de passe</label>
                            <input type="password" class="form-control" name="password" id="password" minlength="8" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary">Connexion</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" data-bs-toggle="modal"
                                data-bs-target="#registerForm">S'inscrire
                        </button>
                        <button type="reset" class="btn btn-secondary">Reset</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="modal fade" id="registerForm" tabindex="-1" aria-labelledby="connexionLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="get" action="/register">
                    <div class="modal-header">
                        <h5 class="modal-title" id="connexionLabel">S'inscrire</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="username" class="form-label">Nom d'utilisateur</label>
                            <input type="text" class="form-control" name="username" id="username" minlength="4" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Mots de passe</label>
                            <input type="password" class="form-control" name="password" id="password" minlength="8" required>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" id="email" class="form-control" name="email">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary">S'inscrire</button>
                        <button type="reset" class="btn btn-secondary">Reset</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
<#else>
    <div class="modal fade" id="createShopForm" tabindex="-1" aria-labelledby="createShopLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="createShopLabel">Créer un magasin</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="username" class="form-label">Nom du magasin</label>
                        <input type="text" class="form-control" name="shopName" id="shopName" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" onclick="submit()" class="btn btn-primary">Créer un magasin</button>
                </div>
            </div>
        </div>
    </div>
    <script>
        function submit() {
            let shopName = document.getElementById("shopName")
            document.location.href = "/createShop?url=" + document.location.pathname + "&shopName=" + shopName.value
        }
    </script>
</#if>
</body>
<script src="/assets/js/bootstrap.min.js"></script>
</html>