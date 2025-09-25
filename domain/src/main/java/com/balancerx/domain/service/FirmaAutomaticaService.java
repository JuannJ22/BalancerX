package com.balancerx.domain.service;

import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.Firma;
import com.balancerx.domain.model.Usuario;
import com.balancerx.domain.valueobject.RolUsuario;

public interface FirmaAutomaticaService {
    Firma firmar(Cuadre cuadre, Usuario usuario, RolUsuario rol);
}
