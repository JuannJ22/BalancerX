# PR parcial sin conflictos (guía rápida)

Si ya tienes cambios locales y te salen errores raros de compilación o conflictos, usa este flujo para aplicar una PR parcial en limpio.

## 1) Guardar tus cambios actuales

```bash
git status
git stash push -u -m "wip-local-antes-de-pr-parcial"
```

> Esto evita mezclar trabajo local con la PR.

## 2) Traer remoto y crear rama limpia

```bash
git fetch --all --prune
git checkout work
git pull --rebase
git checkout -b hotfix/compilacion-limpia
```

## 3) Aplicar solo el fix puntual (sin arrastrar todo)

Opciones:

### A. Cherry-pick de un commit específico

```bash
git cherry-pick <commit_hash>
```

### B. Checkout solo de un archivo

```bash
git checkout work -- src/BalancerX.Application/Servicios/TransferenciaServicio.cs
git add src/BalancerX.Application/Servicios/TransferenciaServicio.cs
git commit -m "Apply isolated compile fix for TransferenciaServicio"
```

## 4) Compilar en limpio

```bash
dotnet clean BalancerX.sln
# Windows PowerShell:
Remove-Item -Recurse -Force src/BalancerX.Application/bin, src/BalancerX.Application/obj -ErrorAction SilentlyContinue
# Bash (si aplica):
# rm -rf src/BalancerX.Application/bin src/BalancerX.Application/obj

dotnet build BalancerX.sln
```

## 5) Recuperar tus cambios locales (si quieres)

```bash
git stash list
git stash pop
```

Si aparecen conflictos al hacer `stash pop`, ya estás al menos sobre una base que compila y puedes resolverlos de forma controlada.

## 6) Checklist mínimo antes de abrir PR

```bash
git status
git log --oneline -n 5
dotnet build BalancerX.sln
```

- La rama debe contener **solo** el cambio puntual.
- Si hay más archivos cambiados de los esperados, separa commits.

---

## Recomendación práctica para tu caso

Para el error de `actualizarTransferenciaRequest`, evita mezclar frontend/SQL/UI en la misma PR.

Haz una PR pequeña que toque únicamente:

- `src/BalancerX.Application/Servicios/TransferenciaServicio.cs`

Después de validarla y mergearla, haces otra PR separada para frontend.
