package com.siscem.portal_sae.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.siscem.portal_sae.dtos.account.AccountCreateDTO;
import com.siscem.portal_sae.dtos.account.AccountDTO;
import com.siscem.portal_sae.dtos.account.AccountEditDTO;
import com.siscem.portal_sae.models.Account;
import com.siscem.portal_sae.models.User;
import com.siscem.portal_sae.models.relationships.AccountServiceTypes;
import com.siscem.portal_sae.models.relationships.IdentificationTypes;
import com.siscem.portal_sae.repositories.AccountRepository;
import com.siscem.portal_sae.repositories.UserRepository;
import com.siscem.portal_sae.repositories.Relationships.AccountServiceTypesRepository;
import com.siscem.portal_sae.repositories.Relationships.IdentificationTypesRepository;

@Service
public class AccountService {

	@Autowired
	AccountRepository cuentaRepository;

	@Autowired
	IdentificationTypesRepository tiposIdentificacionRepository;

	@Autowired
	AccountServiceTypesRepository tiposServicioCuentaRepository;

	@Autowired
	UserRepository usuarioRepository;

	/**
	 * Retrieves a list of account DTOs associated with the given user code. Skips
	 * certain mappings between Account and AccountDTO.
	 * 
	 * @param userCode The code of the user whose accounts are to be retrieved.
	 * @return A list of AccountDTOs.
	 */
	public ResponseEntity<List<AccountDTO>> getAccountsForUser(Integer userCode) {
		List<Account> accounts = cuentaRepository.findByCodigoUsuarioCodigo(userCode);

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Account, AccountDTO> typeMap = modelMapper.createTypeMap(Account.class, AccountDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(AccountDTO::setTipoIdentificacion);
			mp.skip(AccountDTO::setTipoServicio);
			mp.skip(AccountDTO::setCodigoUsuario);
			mp.skip(AccountDTO::setCodigoConsultorAsignado);
		});

		return ResponseEntity.ok(accounts.stream().map(account -> {
			AccountDTO cuentaDTO = modelMapper.map(account, AccountDTO.class);
			cuentaDTO.setTipoIdentificacion(account.getTipoIdentificacion().getNombreTipo());
			cuentaDTO.setTipoServicio(account.getTipoServicio().getNombreTipo());
			cuentaDTO.setCodigoUsuario(account.getCodigoUsuario().getNombre());
			cuentaDTO.setCodigoConsultorAsignado(account.getCodigoConsultorAsignado().getNombre());
			return cuentaDTO;
		}).collect(Collectors.toList()));
	}

	/**
	 * Creates an account based on the provided AccountCreateDTO.
	 * 
	 * @param createAccountDTO The DTO containing account creation data.
	 * @return A response entity indicating the success of the account creation.
	 */
	public ResponseEntity<String> createAccount(AccountCreateDTO createAccountDTO) {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<AccountCreateDTO, Account>() {
			@Override
			protected void configure() {
				skip(destination.getCodigo());
			}
		});
		Account account = modelMapper.map(createAccountDTO, Account.class);
		account.setTipoIdentificacion(
				tiposIdentificacionRepository.findById(createAccountDTO.getTipoIdentificacion()).orElse(null));
		account.setTipoServicio(
				tiposServicioCuentaRepository.findById(createAccountDTO.getTipoServicio()).orElse(null));
		account.setCodigoUsuario(usuarioRepository.findById(createAccountDTO.getCodigoUsuario()).orElse(null));
		account.setCodigoConsultorAsignado(
				usuarioRepository.findById(createAccountDTO.getCodigoConsultorAsignado()).orElse(null));

		if (account.getTipoIdentificacion() == null || account.getTipoServicio() == null
				|| account.getCodigoUsuario() == null || account.getCodigoConsultorAsignado() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
		}

		try {
			cuentaRepository.save(account);
			return ResponseEntity.ok("Cuenta creada");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la cuenta");
		}
	}

	/**
	 * Edits an existing account based on the provided account code and
	 * AccountEditDTO.
	 * 
	 * @param accountCode    The code of the account to be edited.
	 * @param editAccountDTO The DTO containing the updated account data.
	 * @return A response entity indicating the success of the account update.
	 */
	public ResponseEntity<String> editAccount(Integer accountCode, AccountEditDTO editAccountDTO) {
		Optional<Account> existingCuentaOptional = cuentaRepository.findById(accountCode);

		if (existingCuentaOptional.isPresent()) {
			Account existingAccount = existingCuentaOptional.get();

			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			modelMapper.addMappings(new PropertyMap<AccountEditDTO, Account>() {
				@Override
				protected void configure() {
					skip(destination.getCodigo());
				}
			});

			modelMapper.map(editAccountDTO, existingAccount);

			editAccountDTO.getTipoIdentificacion().ifPresent(tipoIdentificacionId -> {
				IdentificationTypes tipoIdentificacion = tiposIdentificacionRepository.findById(tipoIdentificacionId)
						.orElse(null);
				existingAccount.setTipoIdentificacion(tipoIdentificacion);
			});

			editAccountDTO.getTipoServicio().ifPresent(tipoServicioId -> {
				AccountServiceTypes tipoServicio = tiposServicioCuentaRepository.findById(tipoServicioId).orElse(null);
				existingAccount.setTipoServicio(tipoServicio);
			});

			editAccountDTO.getCodigoUsuario().ifPresent(usuarioId -> {
				User usuario = usuarioRepository.findById(usuarioId).orElse(null);
				existingAccount.setCodigoUsuario(usuario);
			});

			editAccountDTO.getCodigoConsultorAsignado().ifPresent(consultorId -> {
				User consultorAsignado = usuarioRepository.findById(consultorId).orElse(null);
				existingAccount.setCodigoConsultorAsignado(consultorAsignado);
			});

			try {
				cuentaRepository.save(existingAccount);
				return ResponseEntity.ok("Cuenta actualizada");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la cuenta");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
		}
	}

	/**
	 * Deletes an account with the given account code.
	 * 
	 * @param accountCode The code of the account to be deleted.
	 * @return A response entity indicating the success of the account deletion.
	 */
	public ResponseEntity<String> deleteAccount(Integer accountCode) {
		try {
			if (cuentaRepository.existsById(accountCode)) {
				cuentaRepository.deleteById(accountCode);
				return ResponseEntity.ok("Cuenta eliminada");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la cuenta");
		}
	}

}
