@echo off
java -Djava.awt.headless=false -Dprism.order=sw -cp "target/classes;target/dependency/*" com.balancerx.BalancerXDesktopApp
pause