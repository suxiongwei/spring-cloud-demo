# spring-cloud-demo

## 演示入口

- 页面地址：`http://localhost:9090/service-demo-redesign.html`
- 如果页面改动未生效，先强刷（`Ctrl+F5`）

## 本地启动（PowerShell）

> 在 PowerShell 中，`-Dspring-boot.run.profiles=...` 需要使用 `--%`，否则会被错误解析。

```powershell
# gateway
cd .\gateway
mvn spring-boot:run --% -Dspring-boot.run.profiles=local
```

```powershell
# service-order-dubbo
cd .\services\service-order-dubbo
mvn spring-boot:run --% -Dspring-boot.run.profiles=local
```

```powershell
# service-product-dubbo
cd .\services\service-product-dubbo
mvn spring-boot:run --% -Dspring-boot.run.profiles=local
```

## 一致性校验命令

```powershell
powershell -ExecutionPolicy Bypass -File scripts/build-validate.ps1
```

```bash
./scripts/build-validate.sh
```

## 面试演示报告（可选）

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run-interview-kit.ps1
```

