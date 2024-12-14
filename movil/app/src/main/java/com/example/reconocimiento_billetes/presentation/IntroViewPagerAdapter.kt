package com.example.reconocimiento_billetes.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.reconocimiento_billetes.R
import com.example.reconocimiento_billetes.domain.ScreenItem

/**
 * Adaptador para el ViewPager que maneja las pantallas introductorias.
 *
 * @param context El contexto de la aplicación.
 * @param listScreen Lista de elementos de tipo [ScreenItem] para mostrar en las pantallas.
 */
class IntroViewPagerAdapter(
    private val context: Context,
    private val listScreen: List<ScreenItem>
) : PagerAdapter() {

    /**
     * Infla la vista de cada ítem del pager y la rellena con los datos correspondientes.
     *
     * @param container El contenedor en el que se añadirá la vista.
     * @param position La posición actual del ítem.
     * @return El objeto asociado con la vista inflada.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutScreen = inflateScreenLayout(container)

        // Asignar los valores del título y la descripción a las vistas correspondientes
        bindScreenData(layoutScreen, position)

        container.addView(layoutScreen)
        return layoutScreen
    }

    /**
     * Infla el diseño de cada pantalla usando el LayoutInflater.
     *
     * @param container El contenedor que recibe la vista inflada.
     * @return La vista inflada.
     */
    private fun inflateScreenLayout(container: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.layout_screen, container, false)
    }

    /**
     * Asigna el título y la descripción a la vista correspondiente.
     *
     * @param layoutScreen La vista inflada a la que se asignan los datos.
     * @param position La posición del elemento actual en la lista.
     */
    private fun bindScreenData(layoutScreen: View, position: Int) {
        val title: TextView = layoutScreen.findViewById(R.id.intro_title)
        val description: TextView = layoutScreen.findViewById(R.id.intro_description)

        val screenItem = listScreen[position]
        title.text = screenItem.title
        description.text = screenItem.description
    }

    /**
     * Devuelve el número de pantallas en el adaptador.
     *
     * @return El tamaño de la lista de pantallas.
     */
    override fun getCount(): Int = listScreen.size

    /**
     * Compara si una vista corresponde al objeto de un ítem.
     *
     * @param view La vista del ítem a comparar.
     * @param `object` El objeto de referencia para comparar.
     * @return `true` si la vista corresponde al objeto, `false` de lo contrario.
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    /**
     * Elimina una vista del contenedor cuando ya no se necesita.
     *
     * @param container El contenedor que contiene la vista.
     * @param position La posición de la vista a eliminar.
     * @param `object` El objeto asociado con la vista a eliminar.
     */
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}