<#import "header.ftl" as head>
<#if data.connected>
    <#import "connectedNavbar.ftl" as nav>
<#else>
    <#import "regularNavbar.ftl" as nav>
</#if>
<#import "form.ftl" as form>
<!DOCTYPE html>
<html lang="fr">
<head>
    <link rel="stylesheet" href="/assets/css/review.css">
    <@head.header>
    </@head.header>
    <title>Shops</title>
</head>
<body>
<@nav.navbar>
</@nav.navbar>
<#switch data.connectedPage.createshoperror>
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
<#list data.shops as shop>
<div class="container py-5 h-100">
    <div class="row d-flex justify-content-center align-items-center h-100">
        <div class="col col-md-9 col-lg-7 col-xl-5">
            <div class="card" style="border-radius: 15px;">
                <div class="card-body p-4">
                    <div class="d-flex text-black">
                        <!--<div class="flex-shrink-0">
                            <img src="https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava1-bg.webp"
                                 alt="Generic placeholder image" class="img-fluid"
                                 style="width: 180px; border-radius: 10px;">
                        </div>-->
                        <div class="flex-grow-1 ms-3">
                            <h5 class="mb-1">${shop.boutique.nom}</h5>
                            <p class="mb-2 pb-1" style="color: #2b2a2a;">${shop.owner}</p>
                            <div class="d-flex justify-content-start rounded-3 p-2 mb-2"
                                 style="background-color: #efefef;">
                                <div>
                                    <p class="small text-muted mb-1">Articles</p>
                                    <p class="mb-0">${shop.numberOfItem}</p>
                                </div>
                                <div class="px-3">
                                    <p class="small text-muted mb-1">Membre</p>
                                    <p class="mb-0">${shop.numberOfMember}</p>
                                </div>
                                <div>
                                    <p class="small text-muted mb-1">Evaluation</p>
                                    <p class="mb-0">${shop.rating}/5</p>
                                </div>
                            </div>
                            <div class="d-flex pt-1">
                                <button type="button" class="btn btn-outline-primary me-1 flex-grow-1"
                                        data-bs-toggle="modal" data-bs-target="#commentShop${shop.boutique.id}">
                                    Commenter
                                </button>
                                <button type="button" onclick="acheter(${shop.boutique.id})"
                                        class="btn btn-primary flex-grow-1">Acheter
                                </button>
                                <div class="modal fade" id="commentShop${shop.boutique.id}" tabindex="-1"
                                     aria-labelledby="createShopLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="commentShopLabel">Commentaire</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                        aria-label="Close"></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="mb-3">
                                                    <label for="commentary" class="form-label">Commentaire</label>
                                                    <textarea type="text" class="form-control" name="commentary"
                                                              id="commentary${shop.boutique.id}"
                                                              onload='this.value.replace("\"","")'>${shop.userRating.commentary}</textarea>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="rating${shop.boutique.id}"
                                                           class="form-label">Note</label>
                                                    <input
                                                            class="rating"
                                                            max="5"
                                                            oninput="changeRating(this)"
                                                            step="0.1"
                                                            name="rating"
                                                            id="rating${shop.boutique.id}"
                                                            style="--value:2.5"
                                                            type="range"
                                                            value="${shop.userRating.rating}">
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" onclick="submitRating(${shop.boutique.id})"
                                                            class="btn btn-primary">
                                                        Commenter
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </#list>
    <@form.formulaire>
    </@form.formulaire>
</body>
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
                        <input type="text" class="form-control" name="username" id="username" minlength="4"
                               required>
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
</#if>
<script src="/assets/js/bootstrap.min.js"></script>
<script src="/assets/js/shops.js"></script>
<script src="/assets/js/review.js"></script>
<script src="/assets/js/connect.js"></script>
</html>