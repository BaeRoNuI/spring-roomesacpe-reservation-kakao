package nextstep.main.java.nextstep.service;

import nextstep.main.java.nextstep.domain.Reservation;
import nextstep.main.java.nextstep.domain.Theme;
import nextstep.main.java.nextstep.domain.ThemeCreateRequestDto;
import nextstep.main.java.nextstep.exception.exception.DuplicateThemeException;
import nextstep.main.java.nextstep.exception.exception.NoSuchThemeException;
import nextstep.main.java.nextstep.exception.exception.RelatedThemeDeletionException;
import nextstep.main.java.nextstep.repository.ReservationRepository;
import nextstep.main.java.nextstep.repository.ThemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThemeServiceTest {

    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ThemeService themeService;

    @Test
    @DisplayName("테마 생성 기능 테스트")
    void createThemeTest() {
        ThemeCreateRequestDto themeCreateRequestDto = new ThemeCreateRequestDto("name", "desc", 0);
        Theme theme = new Theme(1L, themeCreateRequestDto.getName(), themeCreateRequestDto.getDesc(), themeCreateRequestDto.getPrice());

        when(themeRepository.save(any(Theme.class)))
                .thenReturn(theme);
        assertThat(themeService.createTheme(themeCreateRequestDto))
                .isEqualTo(theme);
    }

    @Test
    @DisplayName("중복 이름을 가진 테마 생성 시 예외 발생 테스트")
    void createDuplicateThemeTest() {
        ThemeCreateRequestDto themeCreateRequestDto = new ThemeCreateRequestDto("name", "desc", 0);
        Theme savedTheme = new Theme(1L, themeCreateRequestDto.getName(), "otherDesc", 1000);

        when(themeRepository.findByName(themeCreateRequestDto.getName())).thenReturn(Optional.of(savedTheme));

        assertThatCode(() -> themeService.createTheme(themeCreateRequestDto)).isInstanceOf(DuplicateThemeException.class);
    }

    @Test
    @DisplayName("테마 목록 조회 기능 테스")
    void findAllThemeTest() {
        List<Theme> savedThemeList = List.of(new Theme(1L, "theme1", "theme_desc1", 1000),
                new Theme(2L, "theme2", "theme_desc2", 2000),
                new Theme(3L, "theme3", "theme_desc3", 3000));

        when(themeRepository.findAll()).thenReturn(savedThemeList);

        assertThat(themeService.findAllTheme()).containsExactly(savedThemeList.get(0),
                savedThemeList.get(1),
                savedThemeList.get(2));
    }

    @Test
    @DisplayName("존재하지 않는 테마 삭제시 예외 발생 테스트")
    void deleteThemeTest() {
        Long nonExistThemeId = 0L;
        when(themeRepository.findById(nonExistThemeId)).thenThrow(NoSuchThemeException.class);
        assertThatCode(() -> themeService.deleteTheme(nonExistThemeId)).isInstanceOf(NoSuchThemeException.class);
    }

    @Test
    @DisplayName("존재하는 예약과 관계있는 테마 삭제 시 예외 발생 테스트")
    void deleteRelatedToReservationThemeTest() {
        List<Reservation> reservations = List.of(new Reservation(LocalDate.of(2023, 1, 9), LocalTime.of(1, 30), "reservation1", 1L),
                new Reservation(LocalDate.of(2023, 1, 9), LocalTime.of(1, 30), "reservation2", 1L));

        when(reservationRepository.findAllByThemeId(1L)).thenReturn(reservations);

        assertThatCode(() -> themeService.deleteTheme(1L)).isInstanceOf(RelatedThemeDeletionException.class);
    }
}
