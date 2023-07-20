<#import "header.ftl" as head>
<#if data.connected>
    <#import "connectedNavbar.ftl" as nav>
<#else>
    <#import "regularNavbar.ftl" as nav>
</#if>
<!DOCTYPE html>
<html lang="fr">
<head>
    <@head.header>
    </@head.header>
    <title>Accueil</title>
</head>
<body>
<@nav.navbar>
</@nav.navbar>
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
                <div class="modal-header">
                    <h5 class="modal-title" id="connexionLabel">Connexion</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">

                    <div class="mb-3">
                        <label for="username" class="form-label">Nom d'utilisateur</label>
                        <input type="text" class="form-control" name="username" id="username" minlength="4">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Mots de passe</label>
                        <input type="password" class="form-control" name="password" id="password" minlength="8"
                               required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" onclick="connectSubmit()" class="btn btn-primary">Connexion</button>
                </div>
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
<script src="/assets/js/connect.js"></script>
</html>