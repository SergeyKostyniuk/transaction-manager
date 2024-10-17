package com.khantech.transactionmanager.mapper;

import com.khantech.transactionmanager.dto.TransactionDTO;
import com.khantech.transactionmanager.entity.Transaction;
import com.khantech.transactionmanager.entity.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

  private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

  @ParameterizedTest
  @MethodSource("provideDtoToEntityMappingArguments")
  void shouldMapDtoToEntity(TransactionDTO dto) {
    Transaction result = transactionMapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getId()).isEqualTo(dto.getId());
    assertThat(result.getUserId()).isEqualTo(dto.getUserId());
    assertThat(result.getAmount()).isEqualTo(dto.getAmount());
    assertThat(result.getStatus()).isEqualTo(TransactionStatus.PENDING);
  }

  @ParameterizedTest
  @MethodSource("provideEntityToDtoMappingArguments")
  void shouldMapEntityToDto(Transaction entity) {
    TransactionDTO result = transactionMapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getUserId()).isEqualTo(entity.getUserId());
    assertThat(result.getAmount()).isEqualTo(entity.getAmount());
  }

  @Test
  void shouldReturnNullWhenMapToEntityWithNullDto() {
    Transaction result = transactionMapper.toEntity(null);

    assertThat(result).isNull();
  }

  @Test
  void shouldReturnNullWhenMapToDtoWithNullEntity() {
    TransactionDTO result = transactionMapper.toDto(null);

    assertThat(result).isNull();
  }

  private static Stream<Arguments> provideDtoToEntityMappingArguments() {
    UUID userId1 = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    UUID userId2 = UUID.fromString("b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22");

    final var transactionDTO1 = new TransactionDTO();
    transactionDTO1.setUserId(userId1);
    transactionDTO1.setAmount(BigDecimal.valueOf(100));
    final var transactionDTO2 = new TransactionDTO();
    transactionDTO2.setUserId(userId2);
    transactionDTO2.setAmount(BigDecimal.valueOf(999.99));

    return Stream.of(
        Arguments.of(transactionDTO1),
        Arguments.of(transactionDTO2)
    );
  }

  private static Stream<Arguments> provideEntityToDtoMappingArguments() {
    UUID userId1 = UUID.fromString("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33");
    UUID userId2 = UUID.fromString("d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44");
    UUID transactionId1 = UUID.fromString("e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55");
    UUID transactionId2 = UUID.fromString("f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66");

    return Stream.of(
        Arguments.of(new Transaction(transactionId1, userId1, BigDecimal.valueOf(100), TransactionStatus.APPROVED, LocalDateTime.now())),
        Arguments.of(new Transaction(transactionId2, userId2, BigDecimal.valueOf(999.99), TransactionStatus.PENDING, LocalDateTime.now()))
    );
  }
}
